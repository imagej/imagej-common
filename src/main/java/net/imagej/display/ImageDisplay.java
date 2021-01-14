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
import net.imagej.PositionableByAxis;
import net.imagej.axis.AxisType;
import net.imagej.axis.CalibratedAxis;
import net.imagej.interval.CalibratedRealInterval;
import net.imglib2.Interval;

import org.scijava.display.Display;
import org.scijava.util.RealRect;

/**
 * An image display is a {@link Display} for visualizing {@link Data} objects.
 * It operates directly on {@link DataView}s, which wrap the {@link Data}
 * objects and provide display settings for the data.
 * 
 * @author Curtis Rueden
 * @author Grant Harris
 */
public interface ImageDisplay extends Display<DataView>,
	CalibratedRealInterval<CalibratedAxis>, PositionableByAxis, Interval
{

	/** Pop-up context menu root for image displays. */
	String CONTEXT_MENU_ROOT = "context-ImageDisplay";

	/** Gets the view currently designated as active. */
	DataView getActiveView();

	/** Gets the axis currently designated as active. */
	AxisType getActiveAxis();

	/** Sets the axis currently designated as active. */
	void setActiveAxis(AxisType axis);

	/**
	 * Tests whether the given view should currently be visible in this display. A
	 * view is visible when the current position falls within the bounds of the
	 * view's space, including constant-value dimensions beyond the view's linked
	 * data's space.
	 */
	boolean isVisible(DataView view);
	
	ImageCanvas getCanvas();
	
	/**
	 * Gets a rectangle defining the extents of the image in the current X/Y
	 * plane.
	 */
	RealRect getPlaneExtents();

}
