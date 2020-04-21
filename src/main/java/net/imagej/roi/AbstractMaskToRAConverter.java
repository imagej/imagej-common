/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2020 ImageJ developers.
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

package net.imagej.roi;

import java.lang.reflect.Type;

import net.imglib2.RandomAccessible;
import net.imglib2.roi.Mask;
import net.imglib2.roi.mask.integer.RandomAccessibleAsMask;
import net.imglib2.roi.mask.integer.RandomAccessibleIntervalAsMaskInterval;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.logic.BoolType;

import org.scijava.convert.AbstractConverter;

/**
 * Base class for converting {@link Mask} to {@link RandomAccessible}. This is
 * specifically for converting to {@code RandomAccessible<BoolType} and should
 * not be used for other types.
 *
 * @author Alison Walter
 * @param <M> type converting from, probably Mask or MaskInterval
 * @param <R> type converting to, probably RandomAccessible or
 *          RandomAccessibleInterval
 */
public abstract class AbstractMaskToRAConverter<M extends Mask, R extends RandomAccessible<BoolType>>
	extends AbstractConverter<M, R>
{

	// FIXME: Temporary fix, until the ConvertService uses the TypeService
	@Override
	@Deprecated
	public boolean canConvert(final Class<?> src, final Type dest) {
		return super.canConvert(src, dest) && MaskConversionUtil.isBoolType(dest) &&
			isWrapperMatch(src);
	}

	@Override
	public boolean canConvert(final Object src, final Type dest) {
		return super.canConvert(src, dest) && MaskConversionUtil.isBoolType(dest) &&
			isWrapperMatch(src);
	}

	@Override
	public boolean canConvert(final Object src, final Class<?> dest) {
		return super.canConvert(src, dest) && isWrapperMatch(src);
	}

	@Override
	public boolean canConvert(final Class<?> src, final Class<?> dest) {
		return super.canConvert(src, dest) && isWrapperMatch(src);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T convert(final Object src, final Class<T> dest) {
		if (!getInputType().isInstance(src)) throw new IllegalArgumentException(
			"Cannot convert " + src.getClass() + " to " + getOutputType());
		if (!dest.isAssignableFrom(getOutputType()))
			throw new IllegalArgumentException("Invalid destination class " + dest);

		return (T) convert((M) src);
	}

	public abstract R convert(M src);

	// -- Helper methods --

	/**
	 * Check if the given class is a wrapper, and if so ensure that the input type
	 * is also a wrapper. This ensures that wrappers of {@link BitType} won't fall
	 * through to the general converters.
	 * <p>
	 * In other words, if trying to convert a wrapper only unwrap and do not try
	 * to convert by other means.
	 * </p>
	 */
	private boolean isWrapperMatch(final Class<?> src) {
		if ((src.equals(RandomAccessibleAsMask.class) && !src.equals(
			getInputType()))) return false;
		if (src.equals(RandomAccessibleIntervalAsMaskInterval.class) && !src.equals(
			getInputType())) return false;

		return true;
	}

	/**
	 * Check if the given class is a wrapper, and if so ensure that the input type
	 * is also a wrapper. This ensures that wrappers of {@link BitType} won't fall
	 * through to the general converters.
	 * <p>
	 * In other words, if trying to convert a wrapper only unwrap and do not try
	 * to convert by other means.
	 * </p>
	 */
	private boolean isWrapperMatch(final Object src) {
		return isWrapperMatch(src.getClass());
	}
}
