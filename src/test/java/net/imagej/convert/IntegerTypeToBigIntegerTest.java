/*-
 * #%L
 * ImageJ2 software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2024 ImageJ2 developers.
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

import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.Unsigned12BitType;
import net.imglib2.type.numeric.integer.Unsigned2BitType;
import net.imglib2.type.numeric.integer.Unsigned4BitType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedLongType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scijava.Context;
import org.scijava.convert.ConvertService;

/**
 * Tests the conversion of {@link IntegerType}s into {@link BigInteger}s.
 * 
 * @author Gabriel Selzer
 */
@RunWith(Parameterized.class)
public class IntegerTypeToBigIntegerTest {

	@Parameterized.Parameters(name = "Type conversion test {index}: {0} <=> {1}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] { //
			{ BigInteger.ONE, new UnsignedByteType((byte) 1) }, //
			{ BigInteger.ONE, new Unsigned2BitType((byte) 1) }, //
			{ BigInteger.ONE, new Unsigned4BitType((byte) 1) }, //
			{ BigInteger.ONE, new Unsigned12BitType((byte) 1) }, //
			{ BigInteger.ONE, new UnsignedIntType((byte) 1) }, //
			{ BigInteger.ONE, new UnsignedShortType((byte) 1) }, //
			{ BigInteger.ONE, new UnsignedLongType((byte) 1) }, //
			{ BigInteger.ONE, new ByteType((byte) 1) }, //
			{ BigInteger.ONE, new IntType(1) }, //
			{ BigInteger.ONE, new LongType(1L) }, //
			{ BigInteger.ONE, new ShortType((short) 1) }, //
		});
	}

	@Parameterized.Parameter(0)
	public Object number;

	@Parameterized.Parameter(1)
	public Object integerType;
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

	/** Tests conversion from {@link IntegerType} to {@link BigInteger}. */
	@Test
	public void testIntegerTypeToBigInteger() {
		assertTrue("supports", //
			convertService.supports(integerType, BigInteger.class));
		final BigInteger converted = //
			convertService.convert(integerType, BigInteger.class);
		assertEquals("Converted type value equality", number, converted);
	}
}
