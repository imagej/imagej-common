/*
 * #%L
 * ImageJ2 software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2023 ImageJ2 developers.
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
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Tests {@link EnumeratedAxis}.
 * 
 * @author Curtis Rueden
 */
public class EnumeratedAxisTest extends AbstractAxisTest {

	@Test
	public void testConstructors() {
		final double[] array = {1, 2, 3, 5, 8, 13, 22};
		final EnumeratedAxis axis1 = new EnumeratedAxis(Axes.get("fib"), array);
		assertEquals(5, axis1.calibratedValue(3), 0.0);
		assertEquals(13, axis1.calibratedValue(5), 0.0);

		final List<Integer> list = Arrays.asList(2, 4, 8, 16, 32, 64, 128);
		final EnumeratedAxis axis2 = new EnumeratedAxis(Axes.get("pow"), list);
		assertEquals(16, axis2.calibratedValue(3), 0.0);
		assertEquals(64, axis2.calibratedValue(5), 0.0);
	}

	@Test
	public void testCalibratedValue() {
		final double[] values = {2, 3, 5, 7, 11, 13, 17};
		final EnumeratedAxis axis = new EnumeratedAxis(Axes.get("primes"), values);
		// Verify integer coordinates.
		for (int i = 0; i < values.length; i++) {
			assertEquals(values[i], axis.calibratedValue(i), 0.0);
		}
		// Verify interpolated coordinates.
		assertEquals(4, axis.calibratedValue(1.5), 0.0);
		assertEquals(10.5, axis.calibratedValue(3.875), 0.0);
		// Verify extrapolated coordinates.
		assertEquals(1, axis.calibratedValue(-1), 0.0);
		assertEquals(0.5, axis.calibratedValue(-1.5), 0.0);
		assertEquals(0.25, axis.calibratedValue(-1.75), 0.0);
		assertEquals(0, axis.calibratedValue(-2), 0.0);
		assertEquals(-98, axis.calibratedValue(-100), 0.0);
		assertEquals(21, axis.calibratedValue(7), 0.0);
		assertEquals(23, axis.calibratedValue(7.5), 0.0);
		assertEquals(24.2, axis.calibratedValue(7.8), 0.0);
		assertEquals(25, axis.calibratedValue(8), 0.0);
		assertEquals(393, axis.calibratedValue(100), 0.0);
	}

	@Test
	public void testRawValue() {
		final double[] values = {2, 3, 5, 7, 11, 13, 17};
		final EnumeratedAxis axis = new EnumeratedAxis(Axes.get("primes"), values);
		// Verify integer coordinates.
		for (int i = 0; i < values.length; i++) {
			assertEquals(i, axis.rawValue(values[i]), 0.0);
		}
		// Verify interpolated coordinates.
		assertEquals(1.5, axis.rawValue(4), 0.0);
		assertEquals(3.875, axis.rawValue(10.5), 0.0);
		// Verify extrapolated coordinates.
		assertEquals(-1, axis.rawValue(1), 0.0);
		assertEquals(-1.5, axis.rawValue(0.5), 0.0);
		assertEquals(-1.75, axis.rawValue(0.25), 0.0);
		assertEquals(-2, axis.rawValue(0), 0.0);
		assertEquals(-100, axis.rawValue(-98), 0.0);
		assertEquals(7, axis.rawValue(21), 0.0);
		assertEquals(7.5, axis.rawValue(23), 0.0);
		assertEquals(7.8, axis.rawValue(24.2), 1e-15); // NB: Binary rounding error.
		assertEquals(8, axis.rawValue(25), 0.0);
		assertEquals(100, axis.rawValue(393), 0.0);
	}

	@Test
	public void testWayOutOfBounds() {
		final double[] values = {10, 10.001, 10.999, 11};
		final EnumeratedAxis axis = new EnumeratedAxis(Axes.get("pancakes"), values);
		final double bigValue = 50000;
		final double bigIndex = 1000 * (bigValue - 11) + 3;
		final double smallValue = -50000;
		final double smallIndex = -1000 * (10 - smallValue);
		assertEquals(bigValue, axis.calibratedValue(bigIndex), 1e-7);
		assertEquals(bigIndex, axis.rawValue(bigValue), 1e-4);
		assertEquals(smallValue, axis.calibratedValue(smallIndex), 1e-7);
		assertEquals(smallIndex, axis.rawValue(smallValue), 1e-4);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testNonInvertible() {
		final double[] values = {3, 1, 4, 1, 5, 9, 2, 6};
		final EnumeratedAxis axis = new EnumeratedAxis(Axes.get("pi"), values);
		assertEquals(8, axis.calibratedValue(4.75), 0.0);
		final double index = axis.rawValue(8);
		fail("Unexpectedly successful inverse: " + index);
	}

	@Test
	public void testCopy() {
		final double[] values = {2, 3, 5, 7, 11, 13, 17};
		final EnumeratedAxis axis = new EnumeratedAxis(Axes.get("primes"), values);
		final EnumeratedAxis copy = axis.copy();
		assertNotSame(axis, copy);
		assertEquals(axis, copy);
		for (int i = 0; i < values.length; i++) {
			assertEquals(values[i], copy.calibratedValue(i), 0.0);
		}
		assertEquals(axis.hashCode(), copy.hashCode());
	}
}
