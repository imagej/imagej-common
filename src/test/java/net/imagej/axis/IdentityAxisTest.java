/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2020 Board of Regents of the University of
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
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import org.junit.Test;

/**
 * Tests {@link IdentityAxis}.
 * 
 * @author Barry DeZonia
 */
public class IdentityAxisTest extends AbstractAxisTest {

	@Test
	public void testVarious() {
		IdentityAxis axis = new IdentityAxis();
		assertUnknown(axis);
		axis = new IdentityAxis(Axes.Y);
		assertEquals(Axes.Y, axis.type());
		assertNull(axis.unit());
		axis.setUnit("BAMBALOOIE");
		assertEquals("BAMBALOOIE", axis.unit());
		axis.setType(Axes.CHANNEL);
		assertEquals(Axes.CHANNEL, axis.type());
		assertEquals(5, axis.calibratedValue(5), 0);
		assertEquals(5, axis.rawValue(5), 0);
	}

	@Test
	public void testCopy() {
		final IdentityAxis axis = new IdentityAxis(Axes.Z);
		final IdentityAxis copy = axis.copy();
		assertNotSame(axis, copy);
		assertEquals(axis, copy);
		assertEquals(axis.hashCode(), copy.hashCode());
	}

}
