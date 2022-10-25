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

import java.math.BigInteger;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scijava.Context;
import org.scijava.convert.ConvertService;

import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.*;

/**
 * Tests the conversion of {@link RealType}s into {@link BigInteger}s
 * 
 * @author Gabriel Selzer
 */
@RunWith(Parameterized.class)
public class NumericTypeToBigIntegerTest {

	@Parameterized.Parameters(name = "Type conversion test {index}: {0} <=> {1}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] { //
			{ BigInteger.ZERO, new UnsignedByteType((byte) 0) }, //
			{ BigInteger.ZERO, new Unsigned2BitType((byte) 0) }, //
			{ BigInteger.ZERO, new Unsigned4BitType((byte) 0) }, //
			{ BigInteger.ZERO, new Unsigned12BitType((byte) 0) }, //
			{ BigInteger.ZERO, new UnsignedIntType((byte) 0) }, //
			{ BigInteger.ZERO, new UnsignedShortType((byte) 0) }, //
			{ BigInteger.ZERO, new UnsignedLongType((byte) 0) }, //
			{ BigInteger.ZERO, new ByteType((byte) 0) }, //
			{ BigInteger.ZERO, new IntType(0) }, //
			{ BigInteger.ZERO, new LongType(0L) }, //
			{ BigInteger.ZERO, new ShortType((short) 0) }, //
		});
	}

	@Parameterized.Parameter(0)
	public Object number;

	@Parameterized.Parameter(1)
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
	@Test
	public void testIntegerTypeToBigInteger() {
		assertTrue("Conversion support from number to numeric type", convertService
			.supports(numericType, BigInteger.class));
		BigInteger converted = convertService.convert(numericType,
			BigInteger.class);
		assertEquals("Converted type value equality", number, converted);
	}

}
