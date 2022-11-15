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

import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.integer.*;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

import org.scijava.Priority;
import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;

import java.math.BigInteger;

/**
 * Converters that convert a NumericType to a Number.
 *
 * @author Jan Eglinger
 * @author Gabriel Selzer
 * @author Curtis Rueden
 */
public class NumericTypeToNumberConverters {

	private NumericTypeToNumberConverters() {}

	@Plugin(type = Converter.class)
	public static class ByteTypeToByteConverter extends
		ConciseConverter<ByteType, Byte>
	{

		public ByteTypeToByteConverter() {
			super(ByteType.class, Byte.class, ByteType::get);
		}
	}

	@Plugin(type = Converter.class)
	public static class ShortTypeToShortConverter extends
		ConciseConverter<ShortType, Short>
	{

		public ShortTypeToShortConverter() {
			super(ShortType.class, Short.class, ShortType::get);
		}
	}

	@Plugin(type = Converter.class)
	public static class IntTypeToIntegerConverter extends
		ConciseConverter<IntType, Integer>
	{

		public IntTypeToIntegerConverter() {
			super(IntType.class, Integer.class, IntType::get);
		}
	}

	@Plugin(type = Converter.class)
	public static class LongTypeToLongConverter extends
		ConciseConverter<LongType, Long>
	{

		public LongTypeToLongConverter() {
			super(LongType.class, Long.class, LongType::get);
		}
	}

	@Plugin(type = Converter.class)
	public static class FloatTypeToFloatConverter extends
		ConciseConverter<FloatType, Float>
	{

		public FloatTypeToFloatConverter() {
			super(FloatType.class, Float.class, FloatType::get);
		}
	}

	@Plugin(type = Converter.class)
	public static class DoubleTypeToDoubleConverter extends
		ConciseConverter<DoubleType, Double>
	{

		public DoubleTypeToDoubleConverter() {
			super(DoubleType.class, Double.class, DoubleType::get);
		}
	}

	@Plugin(type = Converter.class)
	public static class UnsignedByteTypeToIntegerConverter extends
		ConciseConverter<UnsignedByteType, Integer>
	{

		public UnsignedByteTypeToIntegerConverter() {
			super(UnsignedByteType.class, Integer.class, UnsignedByteType::get);
		}
	}

	@Plugin(type = Converter.class)
	public static class Unsigned2BitTypeToLongConverter extends
		ConciseConverter<Unsigned2BitType, Long>
	{

		public Unsigned2BitTypeToLongConverter() {
			super(Unsigned2BitType.class, Long.class, Unsigned2BitType::get);
		}
	}

	@Plugin(type = Converter.class)
	public static class Unsigned4BitTypeToLongConverter extends
		ConciseConverter<Unsigned4BitType, Long>
	{

		public Unsigned4BitTypeToLongConverter() {
			super(Unsigned4BitType.class, Long.class, Unsigned4BitType::get);
		}
	}

	@Plugin(type = Converter.class)
	public static class Unsigned12BitTypeToLongConverter extends
		ConciseConverter<Unsigned12BitType, Long>
	{

		public Unsigned12BitTypeToLongConverter() {
			super(Unsigned12BitType.class, Long.class, Unsigned12BitType::get);
		}
	}

	@Plugin(type = Converter.class)
	public static class UnsignedShortTypeToIntegerConverter extends
		ConciseConverter<UnsignedShortType, Integer>
	{

		public UnsignedShortTypeToIntegerConverter() {
			super(UnsignedShortType.class, Integer.class, UnsignedShortType::get);
		}
	}

	@Plugin(type = Converter.class)
	public static class UnsignedIntTypeToLongConverter extends
		ConciseConverter<UnsignedIntType, Long>
	{

		public UnsignedIntTypeToLongConverter() {
			super(UnsignedIntType.class, Long.class, UnsignedIntType::get);
		}
	}

	@Plugin(type = Converter.class, priority = Priority.LOW)
	public static class IntegerTypeToBigIntegerConverter extends
		ConciseConverter<IntegerType, BigInteger>
	{

		public IntegerTypeToBigIntegerConverter() {
			super(IntegerType.class, BigInteger.class, IntegerType::getBigInteger);
		}
	}
}
