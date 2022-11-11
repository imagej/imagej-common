/*
 * #%L
 * ImageJ2 software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2022 ImageJ2 developers.
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

package net.imagej;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;
import net.imagej.axis.CalibratedAxis;
import net.imagej.axis.IdentityAxis;
import net.imagej.display.DataView;
import net.imagej.display.ImageDisplay;
import net.imagej.types.DataTypeService;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converters;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.Unsigned12BitType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

import net.imglib2.view.Views;
import org.scijava.log.LogService;
import org.scijava.object.ObjectService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.script.ScriptService;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

/**
 * Default service for working with {@link Dataset}s.
 * 
 * @author Curtis Rueden
 * @author Barry DeZonia
 * @author Mark Hiner
 */
@Plugin(type = Service.class)
public final class DefaultDatasetService extends AbstractService implements
	DatasetService
{

	@Parameter
	private ScriptService scriptService;

	@Parameter
	private LogService log;

	@Parameter
	private ObjectService objectService;

	// NB: The create(ImgPlus) method instantiates a
	// DefaultDataset, which requires a DataTypeService.
	@Parameter
	private DataTypeService dataTypeService;

	// -- DatasetService methods --

	@Override
	public ObjectService getObjectService() {
		return objectService;
	}

	@Override
	public List<Dataset> getDatasets() {
		return objectService.getObjects(Dataset.class);
	}

	@Override
	public List<Dataset> getDatasets(final ImageDisplay display) {
		final ArrayList<Dataset> datasets = new ArrayList<>();
		if (display != null) {
			for (final DataView view : display) {
				final Data data = view.getData();
				if (!(data instanceof Dataset)) continue;
				final Dataset dataset = (Dataset) data;
				datasets.add(dataset);
			}
		}
		return datasets;
	}

	@Override
	public Dataset create(final long[] dims, final String name,
		final AxisType[] axes, final int bitsPerPixel, final boolean signed,
		final boolean floating)
	{
		return create(dims, name, axes, bitsPerPixel, signed, floating, false);
	}

	@Override
	public Dataset create(final long[] dims, final String name,
		final AxisType[] axes, final int bitsPerPixel, final boolean signed,
		final boolean floating, final boolean virtual)
	{
		if (bitsPerPixel == 1) {
			if (signed || floating) invalidParams(bitsPerPixel, signed, floating);
			return create(new BitType(), dims, name, axes, virtual);
		}
		if (bitsPerPixel == 8) {
			if (floating) invalidParams(bitsPerPixel, signed, floating);
			if (signed) return create(new ByteType(), dims, name, axes, virtual);
			return create(new UnsignedByteType(), dims, name, axes, virtual);
		}
		if (bitsPerPixel == 12) {
			if (signed || floating) invalidParams(bitsPerPixel, signed, floating);
			return create(new Unsigned12BitType(), dims, name, axes, virtual);
		}
		if (bitsPerPixel == 16) {
			if (floating) invalidParams(bitsPerPixel, signed, floating);
			if (signed) return create(new ShortType(), dims, name, axes, virtual);
			return create(new UnsignedShortType(), dims, name, axes, virtual);
		}
		if (bitsPerPixel == 32) {
			if (floating) {
				if (!signed) invalidParams(bitsPerPixel, signed, floating);
				return create(new FloatType(), dims, name, axes, virtual);
			}
			if (signed) return create(new IntType(), dims, name, axes, virtual);
			return create(new UnsignedIntType(), dims, name, axes, virtual);
		}
		if (bitsPerPixel == 64) {
			if (!signed) invalidParams(bitsPerPixel, signed, floating);
			if (floating) return create(new DoubleType(), dims, name, axes, virtual);
			return create(new LongType(), dims, name, axes, virtual);
		}
		invalidParams(bitsPerPixel, signed, floating);
		return null;
	}

	@Override
	public <T extends RealType<T> & NativeType<T>> Dataset create(final T type,
		final long[] dims, final String name, final AxisType[] axes)
	{
		return create(type, dims, name, axes, false);
	}

	@Override
	public <T extends RealType<T> & NativeType<T>> Dataset create(final T type,
		final long[] dims, final String name, final AxisType[] axes,
		final boolean virtual)
	{
		final ImgFactory<T> imgFactory;
		if (virtual) imgFactory = new CellImgFactory<>(type);
		else imgFactory = new PlanarImgFactory<>(type);
		return create(imgFactory, dims, name, axes);
	}

	@Override
	public <T extends RealType<T>> Dataset create(final ImgFactory<T> factory,
		final long[] dims, final String name, final AxisType[] axes)
	{
		final Img<T> img = factory.create(dims);
		final ImgPlus<T> imgPlus = new ImgPlus<>(img, name, axes, null);
		return create(imgPlus);
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T extends Type<T>> Dataset create(final ImgPlus<T> imgPlus) {
		final T type = imgPlus.firstElement();
		if (type instanceof ARGBType) return createARGBType((ImgPlus) imgPlus);
		if (type instanceof RealType) return createRealType((ImgPlus) imgPlus);
		throw new IllegalArgumentException(
			"Only RealType and ARGBType are supported. Given pixel type: " + //
				type.getClass().getName());
	}

	private < T extends RealType< T > > Dataset createRealType(
			ImgPlus<T> imgPlus)
	{
		return new DefaultDataset(getContext(), imgPlus);
	}

	private Dataset createARGBType(ImgPlus<ARGBType> imgPlus) {
		ImgPlus< UnsignedByteType > multiChannel = argbToMultiChannel(imgPlus);
		Dataset dataset = createRealType(multiChannel);
		dataset.setRGBMerged(true);
		return dataset;
	}

	@Override
	public <T extends Type<T>> Dataset create(
			final RandomAccessibleInterval<T> rai)
	{
		return create(ImgPlus.wrap(rai));
	}

	private ImgPlus< UnsignedByteType > argbToMultiChannel(
			ImgPlus< ARGBType > imgPlus)
	{
		RandomAccessibleInterval<ARGBType> rai = imgPlus.getImg();
		RandomAccessibleInterval<UnsignedByteType> red = Converters
				.argbChannel(rai, 1);
		RandomAccessibleInterval<UnsignedByteType> green = Converters.argbChannel(rai, 2);
		RandomAccessibleInterval<UnsignedByteType> blue = Converters.argbChannel(rai, 3);
		Img<UnsignedByteType> channels = ImgPlus.wrapToImg(Views.stack(red, green, blue));
		int n = imgPlus.numDimensions();
		CalibratedAxis[] axes = new CalibratedAxis[n + 1];
		for (int d = 0; d < n; d++) axes[d] = imgPlus.axis(d);
		axes[n] = new IdentityAxis(Axes.CHANNEL);
		return new ImgPlus<>(channels, imgPlus.getName(), axes);
	}

	@Override
	public void initialize() {
		scriptService.addAlias(Dataset.class);
	}

	/**
	 * @deprecated Use io.scif.services.DatasetIOService#canOpen instead.
	 */
	@Override
	@Deprecated
	public boolean canOpen(final String source) {
		throw new UnsupportedOperationException(
			"Use io.scif.services.DatasetIOService instead.");
	}

	/**
	 * @deprecated Use io.scif.services.DatasetIOService#canSave instead.
	 */
	@Override
	@Deprecated
	public boolean canSave(final String destination) {
		throw new UnsupportedOperationException(
			"Use io.scif.services.DatasetIOService instead.");
	}

	/**
	 * @deprecated Use io.scif.services.DatasetIOService#open instead.
	 */
	@Override
	@Deprecated
	public Dataset open(final String source) throws IOException {
		throw new UnsupportedOperationException(
			"Use io.scif.services.DatasetIOService instead.");
	}

	/**
	 * @deprecated Use io.scif.services.DatasetIOService#open instead.
	 */
	@Override
	@Deprecated
	public Dataset open(final String source, final Object config)
		throws IOException
	{
		throw new UnsupportedOperationException(
			"Use io.scif.services.DatasetIOService instead.");
	}

	/**
	 * @deprecated Use io.scif.services.DatasetIOService#revert instead.
	 */
	@Override
	@Deprecated
	public void revert(final Dataset dataset) throws IOException {
		throw new UnsupportedOperationException(
			"Use io.scif.services.DatasetIOService instead.");
	}

	/**
	 * @deprecated Use io.scif.services.DatasetIOService#save instead.
	 */
	@Override
	@Deprecated
	public Object save(final Dataset dataset, final String destination)
		throws IOException
	{
		throw new UnsupportedOperationException(
			"Use io.scif.services.DatasetIOService instead.");
	}

	/**
	 * @deprecated Use io.scif.services.DatasetIOService#save instead.
	 */
	@Override
	@Deprecated
	public Object save(final Dataset dataset, final String destination,
		final Object config) throws IOException
	{
		throw new UnsupportedOperationException(
			"Use io.scif.services.DatasetIOService instead.");
	}

	@Deprecated
	@Override
	public <T extends RealType<T>> Dataset create(final ImgFactory<T> factory,
		final T type, final long[] dims, final String name, final AxisType[] axes)
	{
		final Img<T> img = factory.create(dims, type);
		final ImgPlus<T> imgPlus = new ImgPlus<>(img, name, axes, null);
		return create(imgPlus);
	}

	// -- Helper methods --

	private void invalidParams(final int bitsPerPixel, final boolean signed,
		final boolean floating)
	{
		throw new IllegalArgumentException("Invalid parameters: bitsPerPixel=" +
			bitsPerPixel + ", signed=" + signed + ", floating=" + floating);
	}
}
