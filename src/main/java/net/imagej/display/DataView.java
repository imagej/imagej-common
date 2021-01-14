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

package net.imagej.display;

import net.imagej.Data;
import net.imagej.Dataset;
import net.imagej.ImageJPlugin;
import net.imagej.Position;
import net.imagej.PositionableByAxis;
import net.imagej.axis.AxisType;
import net.imagej.overlay.Overlay;

import org.scijava.Contextual;
import org.scijava.plugin.Plugin;

/**
 * Interface for data views. A view provides visualization settings for an
 * associated {@link Data} object (such as a {@link Dataset} or {@link Overlay})
 * for use with an {@link ImageDisplay}. The view keeps track of the currently
 * visualized position within the N-dimensional data space (and beyond), as well
 * as color settings and other view-specific metadata.
 * <p>
 * For example, a typical 2D display may have a number of sliders enabling a
 * user to select a particular plane of a {@link Dataset} for display. The view
 * keeps track of the current position and provides access to the resultant
 * plane.
 * </p>
 * <p>
 * For dimensions outside the linked {@link Data} object's space, calling
 * {@link #setPosition(long, AxisType)} embeds the view at the specified
 * position along that axis. When the view is part of a display that contains
 * that dimension, the view will only be visible when the display's position for
 * that dimension matches the view's value.
 * </p>
 * <p>
 * Data views discoverable at runtime must implement this interface and be
 * annotated with @{@link Plugin} with attribute {@link Plugin#type()} =
 * {@link DataView}.class. While it possible to create a data view merely by
 * implementing this interface, it is encouraged to instead extend
 * {@link AbstractDataView}, for convenience.
 * </p>
 * 
 * @author Curtis Rueden
 */
public interface DataView extends PositionableByAxis, ImageJPlugin, Contextual {

	/** Gets whether this view is compatible with the given {@link Data}. */
	boolean isCompatible(Data data);

	/**
	 * Initializes the view with the given {@link Data}. This method should only
	 * be called once.
	 */
	void initialize(Data data);

	/** Gets the {@link Data} represented by this view. */
	Data getData();

	// TODO: Evaluate this method's purpose. To change with display refactor in
	// the future.

	/** Gets the N-dimensional plane position of this view. */
	Position getPlanePosition();

	/**
	 * Set the view's selection state.
	 * 
	 * @param isSelected - true if selected, false if not.
	 */
	void setSelected(boolean isSelected);

	/**
	 * @return the view's selection state.
	 */
	boolean isSelected();

	// CTR TODO - reevaluate the methods below, and potentially eliminate some.

	/** Gets the view's natural width in pixels. */
	int getPreferredWidth();

	/** Gets the view's natural height in pixels. */
	int getPreferredHeight();

	/** Updates and redraws the view onscreen. */
	void update();

	/**
	 * Recreates the view. This operation is useful in case the linked
	 * {@link Data} has changed structurally somehow.
	 */
	void rebuild();

	/** Discards the view, performing any needed cleanup. */
	void dispose();

}
