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

import java.lang.reflect.Type;

import net.imagej.roi.ROITree;
import net.imglib2.roi.MaskPredicate;

import org.scijava.convert.AbstractConverter;
import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;
import org.scijava.util.TreeNode;
import org.scijava.util.Types;

/**
 * Abstract {@link Converter} from ROITree to SuperEllipsoid
 *
 * @author Gabriel Selzer
 */
@Plugin(type = Converter.class)
public class ROITreeToMaskPredicateConverter extends
	AbstractConverter<ROITree, MaskPredicate> //
{

	@Override
	public boolean canConvert(final Object src, final Type dest) {
		return canConvert(src, Types.raw(dest));
	}

	@Override
	public boolean canConvert(final Object src, final Class<?> dest) {
		// Assert that src is a ROITree and dest a MaskPredicate
		if (!(src instanceof ROITree)) return false;
		if (!(MaskPredicate.class.isAssignableFrom(dest))) return false;
		ROITree srcTree = (ROITree) src;
		// Assert exactly one ROI in the tree
		if (srcTree.children().size() != 1) return false;
		TreeNode<?> onlyChild = srcTree.children().get(0);
		if (!onlyChild.children().isEmpty()) return false;

		// Assert the only ROI is of the type we are looking for
		return dest.isInstance(onlyChild.data());
	}

	/**
	 * Overriding {@link AbstractConverter#canConvert(Class, Class)} to avoid
	 * failures from that behavior.
	 * 
	 * @param src the {@link Class} converted from
	 * @param dest the {@link Class} converted to
	 * @return true iff this {@link Converter} can convert from {@code from} to
	 *         {@code to}
	 */
	@Override
	public boolean canConvert(final Class<?> src, final Class<?> dest) {
		if (!(ROITree.class.isAssignableFrom(src))) return false;
		if (!(MaskPredicate.class.isAssignableFrom(dest))) return false;
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(final Object src, final Class<T> dest) {
		ROITree srcTree = (ROITree) src;
		return (T) srcTree.children().get(0).data();
	}

	@Override
	public Class<MaskPredicate> getOutputType() {
		return MaskPredicate.class;
	}

	@Override
	public Class<ROITree> getInputType() {
		return ROITree.class;
	}
}
