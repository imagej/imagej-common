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
import net.imagej.display.ImageDisplay;
import net.imagej.display.ImageDisplayService;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.Type;
import net.imglib2.util.Util;

import org.scijava.convert.AbstractConverter;
import org.scijava.convert.Converter;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * This {@code Converter} converts an {@code RandomAccessibleInterval} to an
 * {@code ImageDisplay}. The pixel values will be the labels of the result
 * {@code ImgLabeling}.
 *
 * @author Gabriel Selzer
 */
@SuppressWarnings("rawtypes")
@Plugin(type = Converter.class)
public class RAIToImageDisplayConverter extends
	AbstractConverter<RandomAccessibleInterval, ImageDisplay> {

	@Parameter
	private DatasetService datasetService	;

	@Parameter
	private ImageDisplayService imageDisplayService	;

	@Override
	public <T> T convert(final Object o, final Class<T> aClass) {
		if (!(o instanceof RandomAccessibleInterval)) {
			throw new IllegalArgumentException(
				"Object is not a RandomAccessibleInterval");
		}
		final RandomAccessibleInterval<?> rai = (RandomAccessibleInterval<?>) o;
		Object t = Util.getTypeFromInterval(rai);
		if (!(t instanceof Type)) {
			throw new IllegalArgumentException("Image type is not a Type");
		}
		@SuppressWarnings("unchecked")
		final Dataset dataset = raiToDataset((RandomAccessibleInterval) rai);
		final ImageDisplay imageDisplay = //
			imageDisplayService.createImageDisplay(dataset);
		if (!aClass.isInstance(imageDisplay)) {
			throw new IllegalArgumentException("Class " + aClass.getName() +
				" is incompatible with converted ImageDisplay.");
		}
		@SuppressWarnings("unchecked")
		final T result = (T) imageDisplay;
		return result;
	}

	@Override
	public Class<ImageDisplay> getOutputType() {
		return ImageDisplay.class;
	}

	@Override
	public Class<RandomAccessibleInterval> getInputType() {
		return RandomAccessibleInterval.class;
	}

	private <T extends Type<T>> Dataset raiToDataset(
		final RandomAccessibleInterval<T> rai)
	{
		if (rai instanceof Dataset) return (Dataset) rai;
		if (rai instanceof ImgPlus) {
			final ImgPlus<T> imgPlus = (ImgPlus<T>) rai;
			return datasetService.create(imgPlus);
		}
		return datasetService.create(rai);
	}
}
