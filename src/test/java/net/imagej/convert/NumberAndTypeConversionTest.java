/*-
 * #%L
 * ImageJ2 software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2022 ImageJ2 developers.
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

package net.imagej.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.scijava.Context;
import org.scijava.convert.ConvertService;

import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.type.operators.ValueEquals;

/**
 * Tests the conversion of {@link Number}s into {@link RealType}s (and back).
 * 
 * @author Jan Eglinger
 * @author Gabriel Selzer
 */
@RunWith(Enclosed.class)
public class NumberAndTypeConversionTest {

	@RunWith(Parameterized.class)
	public static class NumberToCorrespondingTypeTest {

		@Parameters(name = "Type conversion test {index}: {0} <=> {1}")
		public static Iterable<Object[]> data() {
			return Arrays.asList(new Object[][] { { Double.POSITIVE_INFINITY,
				new DoubleType(Double.POSITIVE_INFINITY) }, //
				{ Float.NEGATIVE_INFINITY, new FloatType(Float.NEGATIVE_INFINITY) }, //
				{ Integer.MAX_VALUE, new IntType(Integer.MAX_VALUE) }, //
				{ Long.MAX_VALUE, new LongType(Long.MAX_VALUE) }, //
				{ 255, new UnsignedByteType(255) }, //
				// Do one for each RealType //
				{ 0, new UnsignedByteType((byte) 0) }, //
				{ 0L, new Unsigned2BitType((byte) 0) }, //
				{ 0L, new Unsigned4BitType((byte) 0) }, //
				{ 0L, new Unsigned12BitType((byte) 0) }, //
				{ 0L, new UnsignedIntType((byte) 0) }, //
				{ 0, new UnsignedShortType((byte) 0) }, //
				{ 0L, new UnsignedLongType((byte) 0) }, //
				{ (byte) 0, new ByteType((byte) 0) }, //
				{ 0, new IntType(0) }, //
				{ 0L, new LongType(0L) }, //
				{ (short) 0, new ShortType((short) 0) }, //
				{ 0., new DoubleType(0.) }, //
				{ 0.F, new FloatType(0f) }, //
			});
		}

		@Parameter(0)
		public Object number;

		@Parameter(1)
		public Object numericType;

		private ConvertService convertService;

		@Before
		public void setUp() {
			final Context ctx = new Context();
			convertService = ctx.getService(ConvertService.class);
		}

		@After
		public void tearDown() {
			convertService.getContext().dispose();
			convertService = null;
		}

		/**
		 * Test conversion from {@link Number} to {@link NumericType}
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Test
		public void testNumberToNumericType() {
			assertTrue("Conversion support from number to numeric type",
				convertService.supports(number, numericType.getClass()));
			ValueEquals converted = (ValueEquals) convertService.convert(number,
				numericType.getClass());
			assertTrue("Converted type value equality", converted.valueEquals(
				numericType));
		}

		/**
		 * Test conversion from {@link Number} to {@link NumericType}
		 */
		@Test
		public void testNumericTypeToNumber() {
			assertTrue("Conversion support from numeric type to number",
				convertService.supports(numericType, number.getClass()));
			Number converted = (Number) convertService.convert(numericType, number
				.getClass());
			assertEquals("Converted value equality", number, converted);
		}
	}

	public static class NumberToRealTypeTest {

		private ConvertService convertService;

		@Before
		public void setUp() {
			final Context ctx = new Context();
			convertService = ctx.getService(ConvertService.class);
		}

		@After
		public void tearDown() {
			convertService.getContext().dispose();
			convertService = null;
		}

		/**
		 * Test conversion from {@link Number} to {@link RealType} and its
		 * superinterfaces.
		 */
		@Test
		public void testNumberToRealTypeIFaces() {
			List<Number> numbers = Arrays.asList((byte) 1, (short) 1, 1, 1L, 1.F, 1.);
			List<Class<?>> types = Arrays.asList(RealType.class, ComplexType.class,
				NumericType.class);
			for (Number number : numbers) {
				for (Class<?> c : types) {
					assertTrue(convertService.supports(number, c));
					convertService.convert(number, c);
				}
			}
		}
	}
}
