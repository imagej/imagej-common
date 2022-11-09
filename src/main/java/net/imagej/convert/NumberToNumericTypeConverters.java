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

import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;

import net.imglib2.type.numeric.integer.*;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Converters that convert a Number to a NumericType.
 *
 * @author Gabriel Selzer
 */
public class NumberToNumericTypeConverters {

	private NumberToNumericTypeConverters() {}

	@Plugin(type = Converter.class)
	public static class DoubleToDoubleTypeConverter extends
		ConciseConverter<Double, DoubleType>
	{

		public DoubleToDoubleTypeConverter() {
			super(Double.class, DoubleType.class, DoubleType::new);
		}
	}

	@Plugin(type = Converter.class)
	public static class FloatToFloatTypeConverter extends
		ConciseConverter<Float, FloatType>
	{

		public FloatToFloatTypeConverter() {
			super(Float.class, FloatType.class, FloatType::new);
		}
	}

	@Plugin(type = Converter.class)
	public static class ByteToByteTypeConverter extends
		ConciseConverter<Byte, ByteType>
	{

		public ByteToByteTypeConverter() {
			super(Byte.class, ByteType.class, ByteType::new);
		}
	}

	@Plugin(type = Converter.class)
	public static class ShortToShortTypeConverter extends
		ConciseConverter<Short, ShortType>
	{

		public ShortToShortTypeConverter() {
			super(Short.class, ShortType.class, ShortType::new);
		}
	}

	@Plugin(type = Converter.class)
	public static class IntegerToIntTypeConverter extends
		ConciseConverter<Integer, IntType>
	{

		public IntegerToIntTypeConverter() {
			super(Integer.class, IntType.class, IntType::new);
		}
	}

	@Plugin(type = Converter.class)
	public static class LongToLongTypeConverter extends
		ConciseConverter<Long, LongType>
	{

		public LongToLongTypeConverter() {
			super(Long.class, LongType.class, LongType::new);
		}
	}
}
