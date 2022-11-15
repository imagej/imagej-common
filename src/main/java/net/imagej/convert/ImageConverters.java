/*-
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

package net.imagej.convert;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ImgPlus;
import net.imagej.display.DatasetView;
import net.imagej.display.ImageDisplay;
import net.imagej.display.ImageDisplayService;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.Type;
import net.imglib2.util.Util;
import org.scijava.Context;
import org.scijava.Priority;
import org.scijava.convert.Converter;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Converters that convert between image types, including
 * {@link RandomAccessibleInterval}, {@link Img}, {@link ImgPlus},
 * {@link Dataset}, {@link DatasetView}, and {@link ImageDisplay}.
 *
 * @author Curtis Rueden
 * @author Gabriel Selzer
 * @author Mark Hiner hinerm at gmail.com
 */
@SuppressWarnings("rawtypes")
public class ImageConverters {

	private ImageConverters() {
		// NB: Prevent instantiation of utility class.
	}

	// RAI -> Img
	@Plugin(type = Converter.class, priority = Priority.EXTREMELY_LOW)
	public static class RAIToImgConverter extends
		ConciseConverter<RandomAccessibleInterval, Img>
	{

		public RAIToImgConverter() {
			super(RandomAccessibleInterval.class, Img.class,
				ImageConverters::rai2img);
		}
	}

	// RAI -> ImgPlus
	@Plugin(type = Converter.class, priority = Priority.VERY_LOW)
	public static class RAIToImgPlusConverter extends
		ConciseConverter<RandomAccessibleInterval, ImgPlus>
	{

		public RAIToImgPlusConverter() {
			super(RandomAccessibleInterval.class, ImgPlus.class, //
				src -> img2ip(rai2img(src)));
		}
	}

	// RAI -> Dataset
	@Plugin(type = Converter.class, priority = Priority.VERY_LOW)
	public static class RAIToDatasetConverter extends
		ConciseConverter<RandomAccessibleInterval, Dataset>
	{

		@Parameter
		private Context ctx;

		public RAIToDatasetConverter() {
			super(RandomAccessibleInterval.class, Dataset.class, null);
		}

		protected Dataset convert(final RandomAccessibleInterval src) {
			return ip2ds(img2ip(rai2img(src)), ctx);
		}
	}

	// RAI -> DatasetView
	@Plugin(type = Converter.class, priority = Priority.VERY_LOW)
	public static class RAIToDatasetViewConverter extends
		ConciseConverter<RandomAccessibleInterval, DatasetView>
	{

		@Parameter
		private Context ctx;

		public RAIToDatasetViewConverter() {
			super(RandomAccessibleInterval.class, DatasetView.class, null);
		}

		protected DatasetView convert(final RandomAccessibleInterval src) {
			return ds2dv(ip2ds(img2ip(rai2img(src)), ctx));
		}
	}

	// RAI -> ImageDisplay
	@Plugin(type = Converter.class, priority = Priority.VERY_LOW)
	public static class RAIToImageDisplayConverter extends
		ConciseConverter<RandomAccessibleInterval, ImageDisplay>
	{

		@Parameter
		private Context ctx;

		public RAIToImageDisplayConverter() {
			super(RandomAccessibleInterval.class, ImageDisplay.class, null);
		}

		protected ImageDisplay convert(final RandomAccessibleInterval src) {
			return dv2disp(ds2dv(ip2ds(img2ip(rai2img(src)), ctx)));
		}
	}

	// Img -> RAI
	// NB: An Img is already a RAI, so we do not need this converter.

	// Img -> ImgPlus
	@Plugin(type = Converter.class, priority = Priority.LOW)
	public static class ImgToImgPlusConverter extends
		ConciseConverter<Img, ImgPlus>
	{

		public ImgToImgPlusConverter() {
			super(Img.class, ImgPlus.class, ImageConverters::img2ip);
		}
	}

	// Img -> Dataset
	@Plugin(type = Converter.class, priority = Priority.LOW)
	public static class ImgToDatasetConverter extends
		ConciseConverter<Img, Dataset>
	{

		@Parameter
		private Context ctx;

		public ImgToDatasetConverter() {
			super(Img.class, Dataset.class, null);
		}

		protected Dataset convert(final Img src) {
			return ip2ds(img2ip(src), ctx);
		}
	}

	// Img -> DatasetView
	@Plugin(type = Converter.class, priority = Priority.LOW)
	public static class ImgToDatasetViewConverter extends
		ConciseConverter<Img, DatasetView>
	{

		@Parameter
		private Context ctx;

		public ImgToDatasetViewConverter() {
			super(Img.class, DatasetView.class, null);
		}

		protected DatasetView convert(final Img src) {
			return ds2dv(ip2ds(img2ip(src), ctx));
		}
	}

	// Img -> ImageDisplay
	@Plugin(type = Converter.class, priority = Priority.LOW)
	public static class ImgToImageDisplayConverter extends
		ConciseConverter<Img, ImageDisplay>
	{

		@Parameter
		private Context ctx;

		public ImgToImageDisplayConverter() {
			super(Img.class, ImageDisplay.class, null);
		}

		protected ImageDisplay convert(final Img src) {
			return dv2disp(ds2dv(ip2ds(img2ip(src), ctx)));
		}
	}

	// ImgPlus -> Img (or RAI)
	@Plugin(type = Converter.class, priority = Priority.LOW)
	public static class ImgPlusToImgConverter extends
		ConciseConverter<ImgPlus, Img>
	{

		public ImgPlusToImgConverter() {
			super(ImgPlus.class, Img.class, ImageConverters::ip2img);
		}
	}

	// ImgPlus -> Dataset
	@Plugin(type = Converter.class)
	public static class ImgPlusToDatasetConverter extends
		ConciseConverter<ImgPlus, Dataset>
	{

		@Parameter
		private Context ctx;

		public ImgPlusToDatasetConverter() {
			super(ImgPlus.class, Dataset.class, null);
		}

		protected Dataset convert(final ImgPlus src) {
			return ip2ds(src, ctx);
		}
	}

	// ImgPlus -> DatasetView
	@Plugin(type = Converter.class)
	public static class ImgPlusToDatasetViewConverter extends
		ConciseConverter<ImgPlus, DatasetView>
	{

		@Parameter
		private Context ctx;

		public ImgPlusToDatasetViewConverter() {
			super(ImgPlus.class, DatasetView.class, null);
		}

		protected DatasetView convert(final ImgPlus src) {
			return ds2dv(ip2ds(src, ctx));
		}
	}

	// ImgPlus -> ImageDisplay
	@Plugin(type = Converter.class)
	public static class ImgPlusToImageDisplayConverter extends
		ConciseConverter<ImgPlus, ImageDisplay>
	{

		@Parameter
		private Context ctx;

		public ImgPlusToImageDisplayConverter() {
			super(ImgPlus.class, ImageDisplay.class, null);
		}

		protected ImageDisplay convert(final ImgPlus src) {
			return dv2disp(ds2dv(ip2ds(src, ctx)));
		}
	}

	// Dataset -> Img (or RAI)
	@Plugin(type = Converter.class, priority = Priority.LOW)
	public static class DatasetToImgConverter extends
		ConciseConverter<Dataset, Img>
	{

		public DatasetToImgConverter() {
			super(Dataset.class, Img.class, src -> ip2img(ds2ip(src)));
		}
	}

	// Dataset -> ImgPlus
	@Plugin(type = Converter.class)
	public static class DatasetToImgPlusConverter extends
		ConciseConverter<Dataset, ImgPlus>
	{

		public DatasetToImgPlusConverter() {
			super(Dataset.class, ImgPlus.class, ImageConverters::ds2ip);
		}
	}

	// Dataset -> DatasetView
	@Plugin(type = Converter.class)
	public static class DatasetToDatasetViewConverter extends
		ConciseConverter<Dataset, DatasetView>
	{

		public DatasetToDatasetViewConverter() {
			super(Dataset.class, DatasetView.class, src -> {
				final DatasetView view = ds2dv(src);
				view.rebuild(); // NB: Rebuild immediately to construct color tables.
				return view;
			});
		}
	}

	// Dataset -> ImageDisplay
	@Plugin(type = Converter.class)
	public static class DatasetToImageDisplayConverter extends
		ConciseConverter<Dataset, ImageDisplay>
	{

		public DatasetToImageDisplayConverter() {
			super(Dataset.class, ImageDisplay.class, src -> dv2disp(ds2dv(src)));
		}
	}

	// DatasetView -> Img (or RAI)
	@Plugin(type = Converter.class, priority = Priority.LOW)
	public static class DatasetViewToImgConverter extends
		ConciseConverter<DatasetView, Img>
	{

		public DatasetViewToImgConverter() {
			super(DatasetView.class, Img.class, src -> ip2img(ds2ip(dv2ds(src))));
		}
	}

	// DatasetView -> ImgPlus
	@Plugin(type = Converter.class)
	public static class DatasetViewToImgPlusConverter extends
		ConciseConverter<DatasetView, ImgPlus>
	{

		public DatasetViewToImgPlusConverter() {
			super(DatasetView.class, ImgPlus.class, src -> ds2ip(dv2ds(src)));
		}
	}

	// DatasetView -> Dataset
	@Plugin(type = Converter.class)
	public static class DatasetViewToDatasetConverter extends
		ConciseConverter<DatasetView, Dataset>
	{

		public DatasetViewToDatasetConverter() {
			super(DatasetView.class, Dataset.class, ImageConverters::dv2ds);
		}
	}

	// DatasetView -> ImageDisplay
	@Plugin(type = Converter.class)
	public static class DatasetViewToImageDisplayConverter extends
		ConciseConverter<DatasetView, ImageDisplay>
	{

		public DatasetViewToImageDisplayConverter() {
			super(DatasetView.class, ImageDisplay.class, ImageConverters::dv2disp);
		}
	}

	// ImageDisplay -> Img (or RAI)
	@Plugin(type = Converter.class, priority = Priority.LOW)
	public static class ImageDisplayToImgConverter extends
		ConciseConverter<ImageDisplay, Img>
	{

		public ImageDisplayToImgConverter() {
			super(ImageDisplay.class, Img.class, //
				src -> ip2img(ds2ip(dv2ds(disp2dv(src)))));
		}
	}

	// ImageDisplay -> ImgPlus
	@Plugin(type = Converter.class)
	public static class ImageDisplayToImgPlusConverter extends
		ConciseConverter<ImageDisplay, ImgPlus>
	{

		public ImageDisplayToImgPlusConverter() {
			super(ImageDisplay.class, ImgPlus.class, //
				src -> ds2ip(dv2ds(disp2dv(src))));
		}
	}

	// ImageDisplay -> Dataset
	@Plugin(type = Converter.class)
	public static class ImageDisplayToDatasetConverter extends
		ConciseConverter<ImageDisplay, Dataset>
	{

		public ImageDisplayToDatasetConverter() {
			super(ImageDisplay.class, Dataset.class, src -> dv2ds(disp2dv(src)));
		}
	}

	// ImageDisplay -> DatasetView
	@Plugin(type = Converter.class)
	public static class ImageDisplayToDatasetViewConverter extends
		ConciseConverter<ImageDisplay, DatasetView>
	{

		public ImageDisplayToDatasetViewConverter() {
			super(ImageDisplay.class, DatasetView.class, ImageConverters::disp2dv);
		}
	}

	// -- Helper methods - type checking --

	private static <T> void validateImageType(
		final RandomAccessibleInterval<T> rai)
	{
		final Object t = Util.getTypeFromInterval(rai);
		if (!(t instanceof Type)) {
			throw new IllegalArgumentException("Image type '" + t.getClass()
				.getName() + " ' is not a Type");
		}
	}

	// -- Helper methods - wrapping --

	private static <T> Img<T> rai2img(final RandomAccessibleInterval<T> rai) {
		validateImageType(rai);
		return (Img<T>) ImgPlus.wrapToImg((RandomAccessibleInterval) rai);
	}

	private static <T> ImgPlus<T> img2ip(final Img<T> img) {
		return new ImgPlus<>(img);
	}

	private static <T extends Type<T>> Dataset ip2ds(final ImgPlus<T> imgPlus,
		final Context ctx)
	{
		validateImageType(imgPlus);
		return ctx.service(DatasetService.class).create((ImgPlus) imgPlus);
	}

	private static DatasetView ds2dv(final Dataset dataset) {
		final ImageDisplayService imageDisplayService = dataset.context().service(
			ImageDisplayService.class);
		return imageDisplayService.createDatasetView(dataset);
	}

	private static ImageDisplay dv2disp(final DatasetView datasetView) {
		final ImageDisplayService imageDisplayService = datasetView.context()
			.service(ImageDisplayService.class);
		return imageDisplayService.createImageDisplay(datasetView);
	}

	// -- Helper methods - unwrapping --

	private static DatasetView disp2dv(final ImageDisplay imageDisplay) {
		final ImageDisplayService imageDisplayService = imageDisplay.context()
			.service(ImageDisplayService.class);
		return imageDisplayService.getActiveDatasetView(imageDisplay);
	}

	private static Dataset dv2ds(final DatasetView datasetView) {
		return datasetView.getData();
	}

	private static ImgPlus<?> ds2ip(final Dataset dataset) {
		return dataset.getImgPlus();
	}

	private static <T> Img<T> ip2img(final ImgPlus<T> imgPlus) {
		return imgPlus.getImg();
	}

	// NB: We don't need img2rai, because an Img is already a RAI.
	// Technically, an ImgPlus is also already an Img, but it can be nice
	// to have the lightweight Img that the ImgPlus wraps, so we unwrap it.
}
