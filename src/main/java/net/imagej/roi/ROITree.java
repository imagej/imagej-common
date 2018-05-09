/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2013 - 2018 Open Microscopy Environment:
 * 	- Board of Regents of the University of Wisconsin-Madison
 * 	- Glencoe Software, Inc.
 * 	- University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
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
