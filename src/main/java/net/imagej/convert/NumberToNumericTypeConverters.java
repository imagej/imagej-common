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
		NumberToNumericTypeConverter<Double, DoubleType>
	{

		@Override
		public Class<DoubleType> getOutputType() {
			return DoubleType.class;
		}

		@Override
		public Class<Double> getInputType() {
			return Double.class;
		}

		@Override
		protected DoubleType convert(final Double src) {
			return new DoubleType(src);
		}
	}

	@Plugin(type = Converter.class)
	public static class FloatToFloatTypeConverter extends
		NumberToNumericTypeConverter<Float, FloatType>
	{

		@Override
		public Class<FloatType> getOutputType() {
			return FloatType.class;
		}

		@Override
		public Class<Float> getInputType() {
			return Float.class;
		}

		@Override
		protected FloatType convert(final Float src) {
			return new FloatType(src);
		}
	}

	@Plugin(type = Converter.class)
	public static class ByteToByteTypeConverter extends
		NumberToNumericTypeConverter<Byte, ByteType>
	{

		@Override
		public Class<ByteType> getOutputType() {
			return ByteType.class;
		}

		@Override
		public Class<Byte> getInputType() {
			return Byte.class;
		}

		@Override
		protected ByteType convert(final Byte src) {
			return new ByteType(src);
		}
	}

	@Plugin(type = Converter.class)
	public static class ShortToShortTypeConverter extends
		NumberToNumericTypeConverter<Short, ShortType>
	{

		@Override
		public Class<ShortType> getOutputType() {
			return ShortType.class;
		}

		@Override
		public Class<Short> getInputType() {
			return Short.class;
		}

		@Override
		protected ShortType convert(final Short src) {
			return new ShortType(src);
		}
	}

	@Plugin(type = Converter.class)
	public static class IntegerToIntTypeConverter extends
		NumberToNumericTypeConverter<Integer, IntType>
	{

		@Override
		public Class<IntType> getOutputType() {
			return IntType.class;
		}

		@Override
		public Class<Integer> getInputType() {
			return Integer.class;
		}

		@Override
		protected IntType convert(final Integer src) {
			return new IntType(src);
		}
	}

	@Plugin(type = Converter.class)
	public static class LongToLongTypeConverter extends
		NumberToNumericTypeConverter<Long, LongType>
	{

		@Override
		public Class<LongType> getOutputType() {
			return LongType.class;
		}

		@Override
		public Class<Long> getInputType() {
			return Long.class;
		}

		@Override
		protected LongType convert(final Long src) {
			return new LongType(src);
		}
	}

}
