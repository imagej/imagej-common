package net.imagej.convert;

import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;

/**
 * Converters that convert a Number to a NumericType.
 * (Modeled after NumberToNumericTypeConverters)
 *
 * @author Jan Eglinger
 */
public class NumericTypeToNumberConverters {

	private NumericTypeToNumberConverters() {}

	@Plugin(type = Converter.class)
	public static class DoubleTypeToDoubleConverter extends
		NumericTypeToNumberConverter<DoubleType, Double>
	{

		@Override
		public Class<Double> getOutputType() {
			return Double.class;
		}

		@Override
		public Class<DoubleType> getInputType() {
			return DoubleType.class;
		}

		@Override
		protected Double convert(final DoubleType src) {
			return src.get();
		}
	}

	@Plugin(type = Converter.class)
	public static class IntTypeToIntegerConverter extends
		NumericTypeToNumberConverter<IntType, Integer>
	{

		@Override
		public Class<Integer> getOutputType() {
			return Integer.class;
		}

		@Override
		public Class<IntType> getInputType() {
			return IntType.class;
		}

		@Override
		protected Integer convert(final IntType src) {
			return src.get();
		}
	}

	@Plugin(type = Converter.class)
	public static class LongTypeToLongConverter extends
		NumericTypeToNumberConverter<LongType, Long>
	{

		@Override
		public Class<Long> getOutputType() {
			return Long.class;
		}

		@Override
		public Class<LongType> getInputType() {
			return LongType.class;
		}

		@Override
		protected Long convert(final LongType src) {
			return src.get();
		}
	}

	@Plugin(type = Converter.class)
	public static class FloatTypeToFloatConverter extends
		NumericTypeToNumberConverter<FloatType, Float>
	{

		@Override
		public Class<Float> getOutputType() {
			return Float.class;
		}

		@Override
		public Class<FloatType> getInputType() {
			return FloatType.class;
		}

		@Override
		protected Float convert(final FloatType src) {
			return src.get();
		}
	}

	@Plugin(type = Converter.class)
	public static class UnsignedByteTypeToIntegerConverter extends
		NumericTypeToNumberConverter<UnsignedByteType, Integer>
	{

		@Override
		public Class<Integer> getOutputType() {
			return Integer.class;
		}

		@Override
		public Class<UnsignedByteType> getInputType() {
			return UnsignedByteType.class;
		}

		@Override
		protected Integer convert(final UnsignedByteType src) {
			return src.get();
		}
	}
}
