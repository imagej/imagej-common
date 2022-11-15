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

import java.util.Set;

import org.scijava.convert.AbstractConverter;
import org.scijava.convert.Converter;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.util.Types;

import net.imglib2.img.Img;
import net.imglib2.loops.LoopBuilder;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.roi.labeling.LabelingMapping;
import net.imglib2.type.numeric.IntegerType;

/**
 * This {@code Converter} converts an {@code ImgLabeling} to an {@code Img}. If
 * the {@code ImgLabeling} has non-overlapping integer labels, the result
 * {@code Img} will be filled with values corresponding to those labels.
 * Otherwise, {@code ImgLabeling#getIndexImg()} is returned.
 * 
 * @author Jan Eglinger
 * @param <T> the ImgLib2 type used for the index image of the labeling
 */
@SuppressWarnings("rawtypes")
@Plugin(type = Converter.class)
public class ImgLabelingToImgConverter<T extends IntegerType<T>> extends
	ConciseConverter<ImgLabeling, Img>
{

	@Parameter
	private LogService log;

	public ImgLabelingToImgConverter() {
		super(ImgLabeling.class, Img.class, null);
	}

	@Override
	protected Img convert(final ImgLabeling src) {
		ImgLabeling<Integer, T> labeling = (ImgLabeling<Integer, T>) src;
		// check if only singleton labels
		if (!singletonLabels(labeling)) {
			log.warn(
				"Converting ImgLabeling with overlapping labels. Labels cannot be preserved in output, creating continuous integers.");
			return (Img) ((ImgLabeling) labeling).getIndexImg();
		}
		// check if labeling type is integer
		if (!integerLabels(labeling)) {
			log.warn(
				"Converting non-integer label type. Labels cannot be preserved in output, creating continuous integers.");
			return (Img) (labeling).getIndexImg();
		}
		// else, create new img, populate with actual labels
		Img<T> indexImg = (Img<T>) labeling.getIndexImg();
		Img<T> img = indexImg.factory().create(indexImg);
		LabelingMapping<Integer> mapping = labeling.getMapping();
		LoopBuilder.setImages(labeling, img).forEachPixel((l, i) -> {
			Set<Integer> labels = mapping.labelsAtIndex(l.getIndex().getInteger());
			if (!labels.isEmpty()) {
				i.setInteger(labels.iterator().next()); // assuming singleton set here
			}
		});
		return img;
	}

	private boolean integerLabels(ImgLabeling labeling) {
		// Check if the first non-empty label set is an integer
		return Types.isAssignable(labeling.getMapping().labelsAtIndex(1).iterator()
			.next().getClass(), Integer.class);
	}

	private boolean singletonLabels(ImgLabeling labeling) {
		LabelingMapping mapping = labeling.getMapping();
		// TODO should we actually iterate over sets to assert that each is a
		// singleton?
		return mapping.getLabels().size() == mapping.getLabelSets().size() - 1; // the
																																						// empty
																																						// set
																																						// is
																																						// always
																																						// present
	}
}
