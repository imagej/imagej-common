/*-
 * #%L
 * ImageJ2 software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2023 ImageJ2 developers.
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

import org.scijava.convert.AbstractConverter;

import net.imglib2.type.numeric.NumericType;

import java.util.function.Function;

/**
 * A more concise abstract base class for converter plugins.
 *
 * @param <I> input type
 * @param <O> output type
 * @author Gabriel Selzer
 * @author Curtis Rueden
 */
public abstract class ConciseConverter<I, O> extends AbstractConverter<I, O> {

	private final Class<I> inType;
	private final Class<O> outType;
	private final Function<I, O> conversionFunction;

	public ConciseConverter(final Class<I> inType, final Class<O> outType,
		final Function<I, O> conversionFunction)
	{
		this.inType = inType;
		this.outType = outType;
		this.conversionFunction = conversionFunction;
	}

	@Override
	public Class<I> getInputType() {
		return inType;
	}

	@Override
	public Class<O> getOutputType() {
		return outType;
	}

	@Override
	public <T> T convert(final Object src, final Class<T> dest) {
		if (!canConvert(src, dest)) {
			throw new IllegalArgumentException(//
				"Cannot convert source object of type " + src.getClass().getName() + //
					" to destination type " + dest.getName());
		}
		@SuppressWarnings("unchecked")
		final I typedSrc = (I) src;
		final O result = convert(typedSrc);
		@SuppressWarnings("unchecked")
		final T typedResult = (T) result;
		return typedResult;
	}

	protected O convert(I src) {
		return conversionFunction.apply(src);
	}
}
