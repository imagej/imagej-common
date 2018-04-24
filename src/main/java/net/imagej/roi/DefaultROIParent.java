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

import java.util.ArrayList;
import java.util.List;

import net.imagej.DataNode;
import net.imagej.DefaultDataNode;
import net.imglib2.roi.MaskPredicate;

/**
 * Represents a parent {@link DataNode} for a tree of Rois.
 *
 * @author Alison Walter
 */
public class DefaultROIParent implements ROIParent {

	private DataNode<?> parent;
	private final List<DataNode<?>> children;

	/**
	 * Creates a {@link DataNode} which is the parent for a tree of Rois.
	 *
	 * @param parent optional parent
	 * @param children DataNodes with Roi leaves
	 */
	public DefaultROIParent(final DataNode<?> parent,
		final List<DataNode<?>> children)
	{
		if (children == null) throw new IllegalArgumentException(
			"Children required");
		this.parent = parent;
		this.children = children;

		for (final DataNode<?> child : this.children())
			child.setParent(this);
	}

	/**
	 * Creates a {@link DataNode} which is the parent of a tree of Rois.
	 *
	 * @param children list of {@link MaskPredicate}s to be wrapped as
	 *          {@link DataNode}s and stored as the children
	 */
	public DefaultROIParent(final List<MaskPredicate<?>> children) {
		parent = null;
		if (children == null) this.children = new ArrayList<>();
		else {
			this.children = new ArrayList<>(children.size());
			for (final MaskPredicate<?> mp : children)
				this.children.add(new DefaultDataNode<>(mp, this, null));
		}
	}

	@Override
	public DataNode<?> getParent() {
		return parent;
	}

	@Override
	public void setParent(final DataNode<?> parent) {
		this.parent = parent;
	}

	@Override
	public List<DataNode<?>> children() {
		return children;
	}

	@Override
	public Void getData() {
		return null;
	}
}
