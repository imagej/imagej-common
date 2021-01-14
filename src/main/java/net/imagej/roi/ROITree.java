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

package net.imagej.roi;

import java.util.List;

import net.imglib2.roi.MaskPredicate;

import org.scijava.util.DefaultTreeNode;
import org.scijava.util.TreeNode;

/**
 * A tree of ROIs.
 *
 * @author Alison Walter
 * @author Curtis Rueden
 * @see TreeNode
 * @see MaskPredicate
 */
public interface ROITree extends TreeNode<Void> {

	/**
	 * Adds the given list of ROIs to the tree.
	 * 
	 * @param rois list of {@link MaskPredicate}s to be wrapped as
	 *          {@link TreeNode}s and stored as children
	 */
	default void addROIs(final List<MaskPredicate<?>> rois) {
		final List<TreeNode<?>> children = children();
		for (final MaskPredicate<?> roi : rois)
			children.add(new DefaultTreeNode<>(roi, this));
	}

	@Override
	default Void data() {
		return null;
	}

	@Override
	default TreeNode<?> parent() {
		return null;
	}

	@Override
	default void setParent(final TreeNode<?> parent) {
		throw new UnsupportedOperationException();
	}
}
