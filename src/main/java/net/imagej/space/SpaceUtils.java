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

import net.imagej.axis.AxisType;
import net.imagej.axis.TypedAxis;
import net.imglib2.EuclideanSpace;

/**
 * Utility methods for working with {@link EuclideanSpace}, particularly
 * {@link TypedSpace}.
 * 
 * @author Barry DeZonia
 */
public final class SpaceUtils {

	private SpaceUtils() {
		// NB: Prevent instantiation of utility class.
	}

	/**
	 * Gets an array of {@link AxisType}'s delineating the types of the axes of a
	 * {@link TypedSpace}.
	 */
	public static <A extends TypedAxis> AxisType[] getAxisTypes(
		final TypedSpace<A> space)
	{
		final AxisType[] typeList = new AxisType[space.numDimensions()];
		for (int i = 0; i < typeList.length; i++) {
			typeList[i] = space.axis(i).type();
		}
		return typeList;
	}

}
