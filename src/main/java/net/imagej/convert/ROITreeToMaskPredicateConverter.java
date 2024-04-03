/*-
 * #%L
 * ImageJ2 software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2024 ImageJ2 developers.
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
import java.util.function.Function;

import net.imagej.roi.ROITree;
import net.imglib2.roi.MaskPredicate;

import org.scijava.convert.AbstractConverter;
import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;
import org.scijava.util.TreeNode;
import org.scijava.util.Types;

/**
 * {@link Converter} from {@link ROITree} to {@link MaskPredicate}.
 *
 * @author Gabriel Selzer
 * @author Curtis Rueden
 */
@Plugin(type = Converter.class)
public class ROITreeToMaskPredicateConverter extends
	ConciseConverter<ROITree, MaskPredicate>
{

	public ROITreeToMaskPredicateConverter() {
		super(ROITree.class, MaskPredicate.class, src -> (MaskPredicate) src
			.children().get(0).data());
	}

	@Override
	public boolean canConvert(final Object src, final Type dest) {
		// NB: If we don't override this method, the superclass logic invokes
		// canConvert(Class, Class), which does the wrong thing for this
		// instance-sensitive converter, because if we don't have the instance,
		// we can't know whether a particular destination subtype is an acceptable
		// conversion target. We must delegate to a method that knows the instance.
		//
		// This implementation here is probably overkill, and it's hard to be
		// perfect about this with all generic types, but here we check that *all*
		// raw types of the given destination type are acceptable conversion targets
		// for this object. But there are certainly many ways this could go wrong.
		return Types.raws(dest).stream().allMatch(c -> canConvert(src, c));
	}

	@Override
	public boolean canConvert(final Object src, final Class<?> dest) {
		if (!(src instanceof ROITree)) return false;
		// NB: We don't check the dest type versus MaskPredicate here.
		// An instance-insensitive converter cannot guarantee conversion of a
		// ROITree to, say, ClosedWritableEllipsoid, because by types alone we don't
		// know that the ROITree contains a ClosedWritableEllipsoid specifically --
		// all we know is that it contains zero or more MaskPredicates.
		// But this converter is *instance-sensitive*, meaning it promises
		// convertibility to a particular destination type iff the input ROITree's
		// sole child is that type. So we'll check the destination type at the end,
		// after we have extracted the contents of the ROITree.
		@SuppressWarnings("unchecked")
		final ROITree srcTree = (ROITree) src;

		// Assert exactly one ROI in the tree.
		if (srcTree.children().size() != 1) return false;
		final TreeNode<?> onlyChild = srcTree.children().get(0);
		if (!onlyChild.children().isEmpty()) return false;

		// Assert the only ROI is of the type we are looking for.
		return dest.isInstance(onlyChild.data());
	}
}
