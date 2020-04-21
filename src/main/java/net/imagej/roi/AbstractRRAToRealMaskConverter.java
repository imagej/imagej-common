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

import net.imglib2.RealRandomAccessible;
import net.imglib2.roi.RealMask;
import net.imglib2.type.logic.BoolType;

import org.scijava.convert.AbstractConverter;

/**
 * Abstract base class for convert {@link RealRandomAccessible} to
 * {@link RealMask}. This is specifically for
 * {@code RealRandomAccessible<BoolType>} and should not be used for other
 * types.
 *
 * @author Alison Walter
 * @param <R> type converting from, probably RealRandomAccessible or
 *          RealRandomAccessibleRealInterval
 * @param <M> type converting to, probably RealMask or RealMaskRealInterval
 */
public abstract class AbstractRRAToRealMaskConverter<R extends RealRandomAccessible<BoolType>, M extends RealMask>
	extends AbstractConverter<R, M>
{

	@Override
	@SuppressWarnings("unchecked")
	public boolean canConvert(final Object src, final Type dest) {
		return super.canConvert(src, dest) && MaskConversionUtil.isBoolType(
			(R) src);
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean canConvert(final Object src, final Class<?> dest) {
		return super.canConvert(src, dest) && MaskConversionUtil.isBoolType(
			(R) src);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T convert(final Object src, final Class<T> dest) {
		if (!getInputType().isInstance(src)) throw new IllegalArgumentException(
			"Cannot convert " + src.getClass() + " to " + getOutputType());
		if (!dest.isAssignableFrom(getOutputType()))
			throw new IllegalArgumentException("Invalid destination class " + dest);

		return (T) convert((R) src);
	}

	public abstract M convert(R src);

}
