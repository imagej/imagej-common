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

package net.imagej.display.event;

import net.imagej.axis.AxisType;
import net.imagej.display.ImageDisplay;

/**
 * An event indicating that a display's dimensional position has changed. A 2nd
 * step event that follows an AxisPositionEvent. Used to correctly sequence
 * axis position event handling.
 * 
 * @author Barry DeZonia
 */
public class DelayedPositionEvent extends ImageDisplayEvent {

	private final AxisType axis;

	public DelayedPositionEvent(final ImageDisplay display) {
		this(display, display.getActiveAxis());
	}

	public DelayedPositionEvent(final ImageDisplay display, final AxisType axis) {
		super(display);
		if (display.dimensionIndex(axis) < 0) {
			throw new IllegalArgumentException("Invalid axis: " + axis);
		}
		this.axis = axis;
	}

	// -- DelayedPositionEvent methods --

	public AxisType getAxis() {
		return axis;
	}

	// -- Object methods --

	@Override
	public String toString() {
		return super.toString() + "\n\taxis = " + axis;
	}

}
