/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2021 ImageJ developers.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imagej.display;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.imagej.ChannelCollection;
import net.imagej.Data;
import net.imagej.Dataset;
import net.imagej.ImgPlus;
import net.imagej.Position;
import net.imagej.autoscale.AutoscaleService;
import net.imagej.autoscale.DataRange;
import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;
import net.imagej.display.event.DataViewUpdatedEvent;
import net.imagej.display.event.LUTsChangedEvent;
import net.imagej.event.DatasetRGBChangedEvent;
import net.imagej.event.DatasetTypeChangedEvent;
import net.imagej.event.DatasetUpdatedEvent;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.RealLUTConverter;
import net.imglib2.display.ColorTable;
import net.imglib2.display.projector.composite.CompositeXYProjector;
import net.imglib2.display.screenimage.awt.ARGBScreenImage;
import net.imglib2.img.cell.AbstractCellImg;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Binning;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;

import org.scijava.Context;
import org.scijava.event.EventHandler;
import org.scijava.event.EventService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.thread.ThreadService;
import org.scijava.util.ColorRGB;

/**
 * A view into a {@link Dataset}, for use with a {@link ImageDisplay}.
 * 
 * @author Grant Harris
 * @author Curtis Rueden
 * @author Barry DeZonia
 */
@Plugin(type = DataView.class)
public class DefaultDatasetView extends AbstractDataView implements DatasetView
{

	@Parameter
	private AutoscaleService autoscaleService;

	@Parameter
	private ThreadService threadService;

	@Parameter(required = false)
	private EventService eventService;

	/** The dimensional index representing channels, for compositing. */
	private int channelDimIndex;

	/**
	 * Default color tables, one per channel, used when the {@link Dataset} 
	 * doesn't have one for a particular plane.
	 */
	private ArrayList<ColorTable> defaultLUTs;

	private ARGBScreenImage screenImage;

	private CompositeXYProjector<? extends RealType<?>> projector;

	private final ArrayList<RealLUTConverter<? extends RealType<?>>> converters =
		new ArrayList<>();

	// -- DatasetView methods --

	@Override
	public int getChannelCount() {
		if (channelDimIndex < 0) return 1;
		return (int) getData().dimension(channelDimIndex);
	}

	@Override
	public ARGBScreenImage getScreenImage() {
		return screenImage;
	}

	@Override
	public int getCompositeDimIndex() {
		return channelDimIndex;
	}

	@Override
	public CompositeXYProjector<? extends RealType<?>> getProjector() {
		return projector;
	}

	@Override
	public double getChannelMin(final int c) {
		if (!isInitialized()) return Double.NaN;

		return converters.get(c).getMin();
	}

	@Override
	public double getChannelMax(final int c) {
		if (!isInitialized()) return Double.NaN;

		return converters.get(c).getMax();
	}

	@Override
	public void setChannelRange(final int c, final double min, final double max) {
		if (!isInitialized()) return;

		converters.get(c).setMin(min);
		converters.get(c).setMax(max);
	}

	@Override
	public void setChannelRanges(final double min, final double max) {
		for (int c = 0; c < converters.size(); c++) {
			setChannelRange(c, min, max);
		}
	}

	@Override
	public void autoscale(final int c) {
		// get the channel min/max from metadata
		final Dataset data = getData();
		double min = data.getChannelMinimum(c);
		double max = data.getChannelMaximum(c);
		if (Double.isNaN(min) || Double.isNaN(max)) {
			// not provided in metadata, so calculate the min/max
			RandomAccessibleInterval<? extends RealType<?>> interval =
				channelData(data, c);
			interval = xyPlane(interval);
			final DataRange result =
				autoscaleService.getDefaultRandomAccessRange(interval);
			min = result.getMin();
			max = result.getMax();
			// cache min/max in metadata for next time
			data.setChannelMinimum(c, min);
			data.setChannelMaximum(c, max);
		}
		setChannelRange(c, min, max);
	}

	@Override
	public void setComposite(final boolean composite) {
		if (!isInitialized()) return;

		projector.setComposite(composite);
	}

	@Override
	public List<ColorTable> getColorTables() {
		return Collections.unmodifiableList(defaultLUTs);
	}

	@Override
	public void setColorTable(final ColorTable colorTable, final int channel) {
		defaultLUTs.set(channel, colorTable);
		updateLUTs();
		// TODO - temp hacks towards fixing bug #668
		// For now we'll keep this method lightweight and dumb and require
		// callers to map() and update()
		// projector.map();
		// getDisplay().update();
	}

	@Override
	public void resetColorTables(final boolean grayscale) {
		final int channelCount = getChannelCount();
		defaultLUTs.clear();
		defaultLUTs.ensureCapacity(channelCount);
		if (grayscale) {
			for (int i = 0; i < channelCount; i++) {
				defaultLUTs.add(ColorTables.GRAYS);
			}
		}
		else {
			ImgPlus<? extends RealType<?>> imgPlus = getData().getImgPlus();
			for (int c = 0; c < channelCount; c++) {
				ColorTable ct = null;

				// Attempt to use the ColorTables attached to our ImgPlus
				if (imgPlus != null) {
					int planeIndex = 1;
					for (int i=2; i<imgPlus.dimensionIndex(Axes.CHANNEL); i++) {
						planeIndex *= imgPlus.dimension(i);
					}
					planeIndex = planeIndex + c - 1;

					if (planeIndex < imgPlus.getColorTableCount()) ct =
						imgPlus.getColorTable(planeIndex);
				}

				// If we couldn't retrieve a ColorTable, then we use an appropriate
				// default.
				if (ct == null) ct =
					channelCount == 1 ? ColorTables.GRAYS : ColorTables
						.getDefaultColorTable(c);

				defaultLUTs.add(ct);
			}
		}
		updateLUTs();
	}

	@Override
	public ColorMode getColorMode() {
		final boolean composite = projector.isComposite();
		if (composite) {
			return ColorMode.COMPOSITE;
		}
		final List<ColorTable> colorTables = getColorTables();
		for (final ColorTable colorTable : colorTables) {
			// TODO - replace with !ColorTables.isGrayColorTable(colorTable)
			if (colorTable != ColorTables.GRAYS) {
				return ColorMode.COLOR;
			}
		}
		return ColorMode.GRAYSCALE;
	}

	@Override
	public void setColorMode(final ColorMode colorMode) {
		if (!isInitialized()) return;

		resetColorTables(colorMode == ColorMode.GRAYSCALE);
		projector.setComposite(colorMode == ColorMode.COMPOSITE);
		projector.map();
	}

	// TODO - add this kind of mapping code to the Imglib Projector classes. Here
	// it is just a workaround to make modern<->legacy color syncing happy. BDZ

	/**
	 * Reason from a channel collection and internal state what the closest color
	 * is. This is needed for color synchronization with legacy ImageJ.
	 */
	@Override
	public synchronized ColorRGB getColor(final ChannelCollection channels) {
		if (!isInitialized()) return null;

		final int r, g, b;
		final int channelCount = getChannelCount();
		final ColorMode mode = getColorMode();
		if (mode == ColorMode.COMPOSITE) {
			double rSum = 0, gSum = 0, bSum = 0;
			for (int c = 0; c < channelCount; c++) {
				final double value = channels.getChannelValue(c);
				final RealLUTConverter<? extends RealType<?>> converter =
					converters.get(c);
				final double min = converter.getMin();
				final double max = converter.getMax();
				final int grayValue = Binning.valueToBin(256, min, max, value);
				final ColorTable colorTable = converter.getLUT();
				rSum += colorTable.getResampled(ColorTable.RED, 256, grayValue);
				gSum += colorTable.getResampled(ColorTable.GREEN, 256, grayValue);
				bSum += colorTable.getResampled(ColorTable.BLUE, 256, grayValue);
			}
			r = (rSum > 255) ? 255 : (int) Math.round(rSum);
			g = (gSum > 255) ? 255 : (int) Math.round(gSum);
			b = (bSum > 255) ? 255 : (int) Math.round(bSum);
		}
		else { // grayscale or color
			final long currChannel = getLongPosition(Axes.CHANNEL);
			final double value = channels.getChannelValue(currChannel);
			final RealLUTConverter<? extends RealType<?>> converter =
				converters.get((int) currChannel);
			final double min = converter.getMin();
			final double max = converter.getMax();
			final int grayValue = Binning.valueToBin(256, min, max, value);
			if (mode == ColorMode.COLOR) {
				final ColorTable colorTable = converter.getLUT();
				r = colorTable.getResampled(ColorTable.RED, 256, grayValue);
				g = colorTable.getResampled(ColorTable.GREEN, 256, grayValue);
				b = colorTable.getResampled(ColorTable.BLUE, 256, grayValue);
			}
			else { // mode == grayscale
				r = grayValue;
				g = grayValue;
				b = grayValue;
			}
		}
		return new ColorRGB(r, g, b);
	}

	@Override
	public RandomAccessibleInterval<? extends RealType<?>> xyPlane() {
		return xyPlane(getData().getImgPlus());
	}

	@Override
	public RandomAccessibleInterval<? extends RealType<?>> xyPlane(
		final RandomAccessibleInterval<? extends RealType<?>> inputInterval)
	{
		RandomAccessibleInterval<? extends RealType<?>> interval = inputInterval;

		final long[] min = new long[interval.numDimensions()];
		final long[] max = new long[interval.numDimensions()];

		interval.dimensions(max);

		for (int i = 0; i < 2; i++)
			max[i]--;

		for (int i = 2; i < interval.numDimensions(); i++) {
			min[i] = max[i] = getLongPosition(i);
		}

		interval = Views.interval(interval, min, max);

		return interval;
	}

	// -- DataView methods --

	@Override
	public boolean isCompatible(final Data data) {
		return data != null && Dataset.class.isAssignableFrom(data.getClass());
	}

	@Override
	public Dataset getData() {
		return (Dataset) super.getData();
	}

	@Override
	public int getPreferredWidth() {
		return getScreenImage().image().getWidth(null);
	}

	@Override
	public int getPreferredHeight() {
		return getScreenImage().image().getHeight(null);
	}

	@Override
	public void update() {
		publish(new DataViewUpdatedEvent(this));
	}

	@Override
	public synchronized void rebuild() {
		// NB: Make sure any calls to updateLUTs are ignored. If they happen before
		// the converters are correctly defined (in setupProjector) an exception
		// can get thrown. Basically if you add a channel to an image the converter
		// size() can be out of sync.
		uninitializeView();

		channelDimIndex = getChannelDimIndex();

		final ImgPlus<? extends RealType<?>> img = getData().getImgPlus();

		if (defaultLUTs == null || defaultLUTs.size() != getChannelCount()) {
			defaultLUTs = new ArrayList<>();
			resetColorTables(false);
		}

		final int width = (int) img.dimension(0);
		final int height = (int) img.dimension(1);
		screenImage = new ARGBScreenImage(width, height);

		initializeView(isComposite());
		updateLUTs();
		projector.map();
	}

	// -- PositionableByAxis methods --

	@Override
	public long getLongPosition(final AxisType axis) {
		if (!isInitialized()) return 0;

		if (axis.isXY()) return 0;
		final int dim = getData().dimensionIndex(axis);
		if (dim < 0) return 0;
		// It is possible that projector is out of sync with data or view. Choose a
		// sensible default value to avoid exceptions.
		if (dim >= projector.numDimensions()) return 0;
		return projector.getLongPosition(dim);
	}

	@Override
	public void setPosition(final long position, final AxisType axis) {
		if (!isInitialized()) return;

		if (axis.isXY()) return;
		final int dim = getData().dimensionIndex(axis);
		if (dim < 0) return;
		final long currentValue = projector.getLongPosition(dim);
		if (position == currentValue) {
			return; // no change
		}
		projector.setPosition(position, dim);

		// update color tables
		if (dim != channelDimIndex) {
			updateLUTs();
		}

		projector.map();

		super.setPosition(position, axis);
	}

	// -- Event handlers --

	@EventHandler
	protected void onEvent(final DatasetTypeChangedEvent event) {
		if (getData() == event.getObject()) {
			threadService.run(new Runnable() {

				@Override
				public void run() {
					synchronized (getContext()) {
						rebuild();
					}
				}
			});
		}
	}

	@EventHandler
	protected void onEvent(final DatasetRGBChangedEvent event) {
		if (getData() == event.getObject()) {
			threadService.run(new Runnable() {

				@Override
				public void run() {
					synchronized (getContext()) {
						rebuild();
					}
				}
			});
		}
	}

	@EventHandler
	protected void onEvent(final DatasetUpdatedEvent event) {
		// FIXME: eliminate hacky logic here
		if (event instanceof DatasetTypeChangedEvent) {
			return;
		}
		if (event instanceof DatasetRGBChangedEvent) {
			return;
		}
		if (getData() == event.getObject()) {
			if (event.isMetaDataOnly()) return;
			projector.map();
		}
	}

	// -- Helper methods --

	private int getChannelDimIndex() {
		return getData().dimensionIndex(Axes.CHANNEL);
	}

	private boolean isComposite() {
		return getData().getCompositeChannelCount() > 1 || getData().isRGBMerged();
	}

	private boolean isInitialized() {
		return projector != null;
	}

	/** Uninitializes the view. */
	private void uninitializeView() {
		projector = null;
	}

	/** Initializes the view. */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initializeView(final boolean composite) {
		// If there are no converters (e.g. after creation), autoscale them!
		// If there are converters, don't overwrite them!
		if (converters.isEmpty()) {
			final int channelCount = getChannelCount();
			for (int c = 0; c < channelCount; c++) {
				autoscale(c);
				final RealLUTConverter converter =
					new RealLUTConverter(getData().getImgPlus().getChannelMinimum(c),
						getData().getImgPlus().getChannelMaximum(c), null);
				converters.add(converter);
			}
		}

		final ImgPlus<?> img = getData().getImgPlus();

		if (AbstractCellImg.class.isAssignableFrom(img.getImg().getClass())) {
			projector =
				new SourceOptimizedCompositeXYProjector(getData().getImgPlus(),
					screenImage, converters, channelDimIndex);
		}
		else {
			projector =
				new CompositeXYProjector(getData().getImgPlus(), screenImage,
					converters, channelDimIndex);
		}

		projector.setComposite(composite);
	}

	private void updateLUTs() {
		if (!isInitialized()) return;

		final int channelCount = getChannelCount();
		for (int c = 0; c < channelCount; c++) {
			final ColorTable lut = getCurrentLUT(c);
			converters.get(c).setLUT(lut);
		}

		final Context context = getContext();
		if (context == null) return;
		if (eventService == null) return;
		eventService.publishLater(new LUTsChangedEvent(this));
	}

	private ColorTable getCurrentLUT(final int cPos) {
		final Position pos = getPlanePosition();
		if (channelDimIndex >= 0) {
			pos.setPosition(cPos, channelDimIndex - 2);
		}
		final int no = (int) pos.getIndex();
		final ColorTable lut = getData().getColorTable(no);
		if (lut != null) {
			return lut; // return dataset-specific LUT
		}
		return defaultLUTs.get(cPos); // return default channel LUT
	}

	private RandomAccessibleInterval<? extends RealType<?>> channelData(
		final Dataset d, final int c)
	{
		final ImgPlus<? extends RealType<?>> imgPlus = d.getImgPlus();
		final int chIndex = imgPlus.dimensionIndex(Axes.CHANNEL);
		if (chIndex < 0) {
			return imgPlus;
		}
		final long[] mn = new long[d.numDimensions()];
		final long[] mx = Intervals.dimensionsAsLongArray(d);
		for (int i = 0; i < mx.length; i++) {
			mx[i]--;
		}
		mn[chIndex] = c;
		mx[chIndex] = c;
		return Views.interval(imgPlus, mn, mx);
	}

}
