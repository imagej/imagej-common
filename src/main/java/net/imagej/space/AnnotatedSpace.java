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

package net.imagej.space;

import net.imagej.axis.Axis;
import net.imglib2.EuclideanSpace;

/**
 * A Euclidean space with associated metadata about each dimension of the space.
 * The nature of the metadata is left intentionally open-ended; at the topmost
 * level, the {@link Axis} interface provides no additional information about a
 * dimensional axis, but it can be extended to do so.
 * <p>
 * One potential use of the {@link Axis} objects is to store calibration and
 * unit information (see the {@link net.imagej.units} package), but any desired
 * information about the space's dimensions could conceivably be attached.
 * </p>
 *
 * @author Curtis Rueden
 */
public interface AnnotatedSpace<A extends Axis> extends EuclideanSpace {

	/** Gets the axis associated with the given dimension of the space. */
	A axis(int d);

	/** Copies the space's axes into the given array. */
	void axes(A[] axes);

	/** Sets the dimensional axis associated with the given dimension. */
	void setAxis(A axis, int d);

}
