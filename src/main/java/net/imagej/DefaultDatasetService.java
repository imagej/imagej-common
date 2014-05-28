/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2014 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
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

import io.scif.FormatException;
import io.scif.Metadata;
import io.scif.config.SCIFIOConfig;
import io.scif.config.SCIFIOConfig.ImgMode;
import io.scif.img.ImgIOException;
import io.scif.img.ImgOpener;
import io.scif.img.ImgSaver;
import io.scif.img.SCIFIOImgPlus;
import io.scif.services.FormatService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.imagej.display.DataView;
import net.imagej.display.ImageDisplay;
import net.imagej.types.DataTypeService;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.meta.AxisType;
import net.imglib2.meta.ImgPlus;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
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

import org.scijava.log.LogService;
import org.scijava.object.ObjectService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
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
	private LogService log;

	@Parameter
	private ObjectService objectService;

	@Parameter
	private FormatService formatService;

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
		final ArrayList<Dataset> datasets = new ArrayList<Dataset>();
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
		final boolean floating, boolean virtual)
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
	public <T extends RealType<T> & NativeType<T>> Dataset create(
		final T type, final long[] dims, final String name, final AxisType[] axes)
	{
		return create(type, dims, name, axes, false);
	}

	@Override
	public <T extends RealType<T> & NativeType<T>> Dataset create(final T type,
		final long[] dims, final String name, final AxisType[] axes,
		final boolean virtual)
	{
		final ImgFactory<T> imgFactory;
		if (virtual) imgFactory = new CellImgFactory<T>();
		else imgFactory = new PlanarImgFactory<T>();
		return create(imgFactory, type, dims, name, axes);
	}

	@Override
	public <T extends RealType<T>> Dataset create(
		final ImgFactory<T> factory, final T type, final long[] dims,
		final String name, final AxisType[] axes)
	{
		final Img<T> img = factory.create(dims, type);
		final ImgPlus<T> imgPlus = new ImgPlus<T>(img, name, axes, null);
		return create(imgPlus);
	}

	@Override
	public <T extends RealType<T>> Dataset create(final ImgPlus<T> imgPlus) {
		return new DefaultDataset(getContext(), imgPlus);
	}

	@Override
	public boolean canOpen(final String source) {
		try {
			return formatService.getFormat(source, new SCIFIOConfig()
				.checkerSetOpen(true)) != null;
		}
		catch (final FormatException exc) {
			log.error(exc);
		}
		return false;
	}

	@Override
	public boolean canSave(final String destination) {
		try {
			return formatService.getWriterByExtension(destination) != null;
		}
		catch (final FormatException exc) {
			log.error(exc);
		}
		return false;
	}

	@Override
	public Dataset open(final String source) throws IOException {
		final SCIFIOConfig config = new SCIFIOConfig();

		config.imgOpenerSetIndex(0);

		return open(source, config);
	}

	@Override
	public Dataset open(final String source, final SCIFIOConfig config) throws IOException {
		final ImgOpener imageOpener = new ImgOpener(getContext());

		// skip min/max computation
		config.imgOpenerSetComputeMinMax(false);

		// prefer planar array structure, for ImageJ1 and ImgSaver compatibility
		config.imgOpenerSetImgModes(ImgMode.PLANAR);

		try {
			final SCIFIOImgPlus<?> imgPlus =
				imageOpener.openImgs(source, config).get(0);
			@SuppressWarnings({ "rawtypes", "unchecked" })
			final Dataset dataset = create((ImgPlus) imgPlus);
			return dataset;
		}
		catch (final ImgIOException exc) {
			throw new IOException(exc);
		}
	}

	@Override
	public void revert(final Dataset dataset) throws IOException {
		final String source = dataset.getSource();
		if (source == null || source.isEmpty()) {
			// no way to revert
			throw new IOException("Cannot revert image of unknown origin");
		}
		final Dataset revertedDataset = open(source);
		revertedDataset.copyInto(dataset);
	}

	@Override
	public Metadata save(final Dataset dataset, final String destination)
		throws IOException
	{
		return save(dataset, destination, null);
	}

	@Override
	public Metadata save(final Dataset dataset, final String destination,
		final SCIFIOConfig config) throws IOException
	{
		@SuppressWarnings("rawtypes")
		final ImgPlus img = dataset.getImgPlus();

		final Metadata metadata;
		final ImgSaver imageSaver = new ImgSaver(getContext());
		try {
			metadata = imageSaver.saveImg(destination, img, config);
		}
		catch (final ImgIOException exc) {
			throw new IOException(exc);
		}
		catch (final IncompatibleTypeException exc) {
			throw new IOException(exc);
		}

		final String name = new File(destination).getName();
		dataset.setName(name);
		dataset.setDirty(false);

		return metadata;
	}

	// -- Helper methods --

	private void invalidParams(final int bitsPerPixel,
		final boolean signed, final boolean floating)
	{
		throw new IllegalArgumentException("Invalid parameters: bitsPerPixel=" +
			bitsPerPixel + ", signed=" + signed + ", floating=" + floating);
	}
}
