/*-
 * #%L
 * ImageJ2 software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2023 ImageJ2 developers.
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

import org.scijava.convert.AbstractConverter;
import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgView;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.type.numeric.IntegerType;

import java.util.RandomAccess;
import java.util.function.Function;

/**
 * This {@code Converter} converts an {@code Img} to an {@code ImgLabeling}. The
 * pixel values will be the labels of the result {@code ImgLabeling}.
 * 
 * @author Jan Eglinger
 * @author Curtis Rueden
 * @param <T> the ImgLib2 type of the input {@code Img} and the index image of
 *          the resulting {@code ImgLabeling}
 */
@SuppressWarnings("rawtypes")
@Plugin(type = Converter.class)
public class RandomAccessibleIntervalToImgLabelingConverter<T extends IntegerType<T>>
	extends ConciseConverter<RandomAccessibleInterval, ImgLabeling>
{

	public RandomAccessibleIntervalToImgLabelingConverter() {
		super(RandomAccessibleInterval.class, ImgLabeling.class, src -> {
			final RandomAccessibleInterval<T> rai = (RandomAccessibleInterval<T>) src;
			final Img<T> indexImg = ImgView.wrap(rai).factory().create(rai);
			final ImgLabeling<Integer, T> labeling = new ImgLabeling<>(indexImg);

			LoopBuilder.setImages(rai, labeling).forEachPixel((i, l) -> {
				int v = i.getInteger();
				if (v != 0) l.add(v);
			});

			return labeling;
		});
	}
}
