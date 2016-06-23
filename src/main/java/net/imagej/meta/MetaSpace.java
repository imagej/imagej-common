/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2016 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
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

package net.imagej.meta;

import net.imagej.meta.MetaViews.SimpleItem;
import net.imagej.meta.MetaViews.VaryingItem;
import net.imglib2.EuclideanSpace;
import net.imglib2.RandomAccessible;

/**
 * A structured collection of metadata associated with an N-dimensional space.
 * <p>
 * The structure consists of the following layers:
 * </p>
 * <ul>
 * <li>Metadata objects at global scope. E.g.: a global color table.</li>
 * <li>Metadata objects specific to a dimensional axis. E.g.: X calibration
 * attached to X axis.</li>
 * <li>Metadata objects specific to a combination of axes. E.g.: Attribute
 * "spatial" attached to X,Y,Z axes in an XYZTC image.</li>
 * <li>Metadata objects which vary with one or more axes. E.g.: A per-slice
 * color table.</li>
 * </ul>
 * <p>
 * 
 * @author Curtis Rueden
 */
public interface MetaSpace extends EuclideanSpace {

	// -- Metadata object mutators --

	default <T> void set(final Class<T> type, final T value, final int... axes) {
		item(axes).get().put(type, value);
	}

	<T> void set(final Class<T> type, final RandomAccessible<T> values,
		final boolean[] variesWithAxes, final int... axes);

	// -- Metadata object accessors --

	/**
	 * Retrieves the metadata of the given type associated with the specified
	 * combination of axes, or null if none.
	 * <ul>
	 * <li>Metadata of global scope&mdash;e.g., a global color table&mdash;can be
	 * retrieved by calling {@code get(type)} with no axes.</li>
	 * <li>Metadata specific to a dimensional axis&mdash;e.g., the first axis's
	 * physical calibration&mdash;can be retrieved by calling
	 * {@code get(type, axis)}.</li>
	 * <li>Metadata associated with a combination of axes&mdash;e.g., a "spatial"
	 * attribute attached to the X,Y,Z axes of an XYZTC image&mdash;can be
	 * retrieved by calling {@code get(type, axis1, axis2, ...)}.</li>
	 * </ul>
	 */
	default <T> T value(final Class<T> type, final int... axes) {
		return item(axes).getAt(null).get(type);
	}

	/**
	 * Retrieves a {@link RandomAccessible} which houses position-variant metadata
	 * of the given type, or null if none.
	 */
	default <T> RandomAccessible<T> values(final Class<T> type) {
		return item(type).get();
	}

	// -- MetaViews item accessors --

	/**
	 * Gets the {@link SimpleItem} associated with the given combination of axes,
	 * creating it if necessary.
	 */
	SimpleItem<MetaObjects> item(final int... axes);

	/** Gets the {@link VaryingItem} of the given type, or null if none. */
	<T> VaryingItem<T> item(final Class<T> type);

}
