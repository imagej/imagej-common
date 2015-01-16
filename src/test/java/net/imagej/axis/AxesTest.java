/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2015 Board of Regents of the University of
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

package net.imagej.axis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.Comparator;

import org.junit.Test;

/**
 * Tests {@link Axes}.
 * 
 * @author Curtis Rueden
 */
public class AxesTest {

	/** Tests {@link Axes#knownTypes()}. */
	@Test
	public void testKnownTypes() {
		final AxisType[] knownTypes = Axes.knownTypes();
		assertNotNull(knownTypes);
		assertEquals(5, knownTypes.length);
		sort(knownTypes);
		assertSame(Axes.CHANNEL, knownTypes[0]);
		assertSame(Axes.TIME, knownTypes[1]);
		assertSame(Axes.X, knownTypes[2]);
		assertSame(Axes.Y, knownTypes[3]);
		assertSame(Axes.Z, knownTypes[4]);
	}

	private void sort(final AxisType[] axisTypes) {
		Arrays.sort(axisTypes, 0, axisTypes.length, new Comparator<AxisType>() {

			@Override
			public int compare(final AxisType o1, final AxisType o2) {
				return o1.getLabel().compareTo(o2.getLabel());
			}

		});
	}

}
