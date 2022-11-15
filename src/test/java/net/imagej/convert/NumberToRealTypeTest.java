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

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import net.imglib2.type.BooleanType;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.scijava.Context;
import org.scijava.convert.ConvertService;

/**
 * Tests conversion of {@link Number}s to {@link RealType}s.
 *
 * @author Gabriel Selzer
 * @see NumberToAndFromRealTypeTest
 */
public class NumberToRealTypeTest {

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
	 * Tests conversion from {@link Number} to {@link RealType} and its
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

	@Test
	public void testNumberToIntegerType() {
		List<Number> numbers = Arrays.asList((byte) 1, (short) 1, 1, 1L);
		for (Number number : numbers) {
			assertTrue(convertService.supports(number, IntegerType.class));
			convertService.convert(number, IntegerType.class);
		}
	}

	@Test
	public void testBooleanToBooleanType() {
		Boolean b = true;
		assertTrue(convertService.supports(b, BooleanType.class));
		convertService.convert(b, BooleanType.class);
	}
}
