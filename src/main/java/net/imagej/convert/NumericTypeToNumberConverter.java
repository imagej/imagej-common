package net.imagej.convert;

import net.imglib2.type.numeric.NumericType;

import org.scijava.convert.AbstractConverter;

/**
 * Abstract class for Converters that convert a NumericType to a Number.
 * (Modeled after NumberToNumericTypeConverter)
 *
 * @param <I> input type
 * @param <O> output type
 * @author Jan Eglinger
 */
public abstract class NumericTypeToNumberConverter<I extends NumericType<I>, O extends Number>
	extends AbstractConverter<I, O>
{

	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(final Object src, final Class<T> dest) {
		return (T) convert((I) src);
	}

	protected abstract O convert(final I src);

}
