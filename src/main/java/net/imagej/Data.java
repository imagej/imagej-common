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

package net.imagej;

import net.imagej.axis.CalibratedAxis;
import net.imagej.interval.CalibratedRealInterval;
import net.imagej.overlay.Overlay;

import org.scijava.Contextual;
import org.scijava.Named;

/**
 * A data object is the superinterface for all types of N-dimensional data
 * displayable in a cartesian space. See {@link Dataset} and {@link Overlay} for
 * more specific implementations.
 * 
 * @author Curtis Rueden
 */
public interface Data extends CalibratedRealInterval<CalibratedAxis>,
	Contextual, Named
{

	/**
	 * Informs interested parties that the data object has undergone a
	 * non-structural change, such as sample values being updated.
	 * 
	 * @see net.imagej.event.DataUpdatedEvent
	 */
	void update();

	/**
	 * Informs interested parties that the data object has undergone a major
	 * change, such as the dimensional extents changing.
	 * 
	 * @see net.imagej.event.DataRestructuredEvent
	 */
	void rebuild();

	/**
	 * Adds to the data object's reference count. Typically this is called when
	 * the data object is added to a display. Implementers of this interface may
	 * want to emit Events when the reference count goes to 1.
	 * {@link AbstractData} does exactly this to let interested parties know that
	 * a data object has come into use.
	 */
	void incrementReferences();

	/**
	 * Subtracts from the data object's reference count. Typically this is called
	 * when the data object is removed from a display. Implementers of this
	 * interface may want to emit Events when the reference count goes to 0.
	 * {@link AbstractData} does exactly this to let interested parties know that
	 * the data object is no longer in use.
	 */
	void decrementReferences();

}
