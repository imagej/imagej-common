/*-
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
package net.imagej.convert;

import org.scijava.convert.AbstractConverter;
import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;

import net.imglib2.Cursor;
import net.imglib2.img.Img;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.roi.labeling.LabelingType;
import net.imglib2.type.numeric.IntegerType;

/**
 * This {@code Converter} converts an {@code Img} to an {@code ImgLabeling}. The
 * pixel values will be the labels of the result {@code ImgLabeling}.
 * 
 * @author Jan Eglinger
 *
 * @param <T>
 *            the ImgLib2 type of the input {@code Img} and the index image of
 *            the resulting {@code ImgLabeling}
 */
@SuppressWarnings("rawtypes")
@Plugin(type = Converter.class)
public class ImgToImgLabelingConverter<T extends IntegerType<T>> extends AbstractConverter<Img, ImgLabeling> {

	@SuppressWarnings("unchecked")
	@Override
	public <L> L convert(Object img, Class<L> type) {
		Img<T> indexImg = ((Img<T>) img).factory().create(((Img<T>) img));
		ImgLabeling<Integer, T> labeling = new ImgLabeling<>(indexImg);

		LoopBuilder.setImages(((Img<T>) img), labeling).forEachPixel((i, l) -> {
			int v = i.getInteger();
			if (v != 0)
				l.add(v);
		});

//		Cursor<T> imgCursor = ((Img<T>) img).cursor();
//		Cursor<LabelingType<Integer>> labelCursor = labeling.cursor();
//		while (imgCursor.hasNext()) {
//			T value = imgCursor.next();
//			if (value.getInteger() == 0) {
//				labelCursor.fwd();
//			} else {
//				labelCursor.next().add(value.getInteger());
//			}
//		}

		return (L) labeling;
	}

	@Override
	public Class<Img> getInputType() {
		return Img.class;
	}

	@Override
	public Class<ImgLabeling> getOutputType() {
		return ImgLabeling.class;
	}

}
