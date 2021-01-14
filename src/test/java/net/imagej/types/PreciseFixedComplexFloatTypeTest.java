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

package net.imagej.types;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test code for the {@link PreciseFixedComplexFloatType} type.
 * 
 * @author Barry DeZonia
 */
public class PreciseFixedComplexFloatTypeTest {

	private static final double PRECISION = 0.0000000000001;

	@Test
	public void testAtan2() {
		PreciseFixedComplexFloatType val;

		// the 90s

		val = new PreciseFixedComplexFloatType(1, 0);
		assertEquals(0, val.getPhaseDouble(), PRECISION);
		val = new PreciseFixedComplexFloatType(0, 1);
		assertEquals(Math.PI / 2, val.getPhaseDouble(), PRECISION);
		val = new PreciseFixedComplexFloatType(-1, 0);
		assertEquals(Math.PI, val.getPhaseDouble(), PRECISION);
		val = new PreciseFixedComplexFloatType(0, -1);
		assertEquals(3 * Math.PI / 2, val.getPhaseDouble(), PRECISION);

		// the 45s

		val = new PreciseFixedComplexFloatType(1, 1);
		assertEquals(Math.PI / 4, val.getPhaseDouble(), PRECISION);
		val = new PreciseFixedComplexFloatType(-1, 1);
		assertEquals(3 * Math.PI / 4, val.getPhaseDouble(), PRECISION);
		val = new PreciseFixedComplexFloatType(-1, -1);
		assertEquals(5 * Math.PI / 4, val.getPhaseDouble(), PRECISION);
		val = new PreciseFixedComplexFloatType(1, -1);
		assertEquals(7 * Math.PI / 4, val.getPhaseDouble(), PRECISION);

		// the 30s

		double half = 0.5;
		double root3over2 = Math.sqrt(3) / 2;

		val = new PreciseFixedComplexFloatType(half, root3over2);
		assertEquals(2 * Math.PI / 6, val.getPhaseDouble(), PRECISION);
		val = new PreciseFixedComplexFloatType(-half, root3over2);
		assertEquals(4 * Math.PI / 6, val.getPhaseDouble(), PRECISION);
		val = new PreciseFixedComplexFloatType(half, -root3over2);
		assertEquals(10 * Math.PI / 6, val.getPhaseDouble(), PRECISION);
		val = new PreciseFixedComplexFloatType(-half, -root3over2);
		assertEquals(8 * Math.PI / 6, val.getPhaseDouble(), PRECISION);

		val = new PreciseFixedComplexFloatType(root3over2, half);
		assertEquals(Math.PI / 6, val.getPhaseDouble(), PRECISION);
		val = new PreciseFixedComplexFloatType(-root3over2, half);
		assertEquals(5 * Math.PI / 6, val.getPhaseDouble(), PRECISION);
		val = new PreciseFixedComplexFloatType(root3over2, -half);
		assertEquals(11 * Math.PI / 6, val.getPhaseDouble(), PRECISION);
		val = new PreciseFixedComplexFloatType(-root3over2, -half);
		assertEquals(7 * Math.PI / 6, val.getPhaseDouble(), PRECISION);
	}

	@Test
	public void testSqrt() {
		PreciseFixedComplexFloatType val;

		val = new PreciseFixedComplexFloatType(1, 0);
		assertEquals(1, val.getPowerDouble(), PRECISION);

		val = new PreciseFixedComplexFloatType(2, 0);
		assertEquals(2, val.getPowerDouble(), PRECISION);

		val = new PreciseFixedComplexFloatType(3, 0);
		assertEquals(3, val.getPowerDouble(), PRECISION);

		val = new PreciseFixedComplexFloatType(4, 0);
		assertEquals(4, val.getPowerDouble(), PRECISION);

		val = new PreciseFixedComplexFloatType(0, 1);
		assertEquals(1, val.getPowerDouble(), PRECISION);

		val = new PreciseFixedComplexFloatType(0, 2);
		assertEquals(2, val.getPowerDouble(), PRECISION);

		val = new PreciseFixedComplexFloatType(0, 3);
		assertEquals(3, val.getPowerDouble(), PRECISION);

		val = new PreciseFixedComplexFloatType(0, 4);
		assertEquals(4, val.getPowerDouble(), PRECISION);

		val = new PreciseFixedComplexFloatType(1, 1);
		assertEquals(Math.sqrt(2), val.getPowerDouble(), PRECISION);

		val = new PreciseFixedComplexFloatType(2, 2);
		assertEquals(2 * Math.sqrt(2), val.getPowerDouble(), PRECISION);

		val = new PreciseFixedComplexFloatType(3, 3);
		assertEquals(3 * Math.sqrt(2), val.getPowerDouble(), PRECISION);

		val = new PreciseFixedComplexFloatType(4, 4);
		assertEquals(4 * Math.sqrt(2), val.getPowerDouble(), PRECISION);
	}
}
