package net.imagej.convert;

import net.imglib2.type.numeric.integer.*;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;

/**
 * Converters that convert a Number to a NumericType.
 *
 * @author Jan Eglinger
 * @author Gabriel Selzer
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
	public static class ByteTypeToByteConverter extends
			NumericTypeToNumberConverter<ByteType, Byte>
	{

		@Override
		public Class<Byte> getOutputType() {
			return Byte.class;
		}

		@Override
		public Class<ByteType> getInputType() {
			return ByteType.class;
		}

		@Override
		protected Byte convert(final ByteType src) {
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
	public static class ShortTypeToShortConverter extends
			NumericTypeToNumberConverter<ShortType, Short>
	{

		@Override
		public Class<Short> getOutputType() {
			return Short.class;
		}

		@Override
		public Class<ShortType> getInputType() {
			return ShortType.class;
		}

		@Override
		protected Short convert(final ShortType src) {
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

	@Plugin(type = Converter.class)
	public static class Unsigned2BitTypeToLongConverter extends
			NumericTypeToNumberConverter<Unsigned2BitType, Long>
	{

		@Override
		public Class<Long> getOutputType() {
			return Long.class;
		}

		@Override
		public Class<Unsigned2BitType> getInputType() {
			return Unsigned2BitType.class;
		}

		@Override
		protected Long convert(final Unsigned2BitType src) {
			return src.get();
		}
	}

	@Plugin(type = Converter.class)
	public static class Unsigned4BitTypeToLongConverter extends
			NumericTypeToNumberConverter<Unsigned4BitType, Long>
	{

		@Override
		public Class<Long> getOutputType() {
			return Long.class;
		}

		@Override
		public Class<Unsigned4BitType> getInputType() {
			return Unsigned4BitType.class;
		}

		@Override
		protected Long convert(final Unsigned4BitType src) {
			return src.get();
		}
	}

	@Plugin(type = Converter.class)
	public static class Unsigned12BitTypeToLongConverter extends
			NumericTypeToNumberConverter<Unsigned12BitType, Long>
	{

		@Override
		public Class<Long> getOutputType() {
			return Long.class;
		}

		@Override
		public Class<Unsigned12BitType> getInputType() {
			return Unsigned12BitType.class;
		}

		@Override
		protected Long convert(final Unsigned12BitType src) {
			return src.get();
		}
	}

	@Plugin(type = Converter.class)
	public static class UnsignedIntTypeToLongConverter extends
			NumericTypeToNumberConverter<UnsignedIntType, Long>
	{

		@Override
		public Class<Long> getOutputType() {
			return Long.class;
		}

		@Override
		public Class<UnsignedIntType> getInputType() {
			return UnsignedIntType.class;
		}

		@Override
		protected Long convert(final UnsignedIntType src) {
			return src.get();
		}
	}

	@Plugin(type = Converter.class)
	public static class UnsignedShortTypeToIntegerConverter extends
			NumericTypeToNumberConverter<UnsignedShortType, Integer>
	{

		@Override
		public Class<Integer> getOutputType() {
			return Integer.class;
		}

		@Override
		public Class<UnsignedShortType> getInputType() {
			return UnsignedShortType.class;
		}

		@Override
		protected Integer convert(final UnsignedShortType src) {
			return src.get();
		}
	}

	@Plugin(type = Converter.class)
	public static class UnsignedLongTypeToLongConverter extends
			NumericTypeToNumberConverter<UnsignedLongType, Long>
	{

		@Override
		public Class<Long> getOutputType() {
			return Long.class;
		}

		@Override
		public Class<UnsignedLongType> getInputType() {
			return UnsignedLongType.class;
		}

		@Override
		protected Long convert(final UnsignedLongType src) {
			return src.get();
		}
	}
}
