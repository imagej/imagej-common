/*
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

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Function;

import net.imglib2.FinalInterval;

import org.scijava.Priority;
import org.scijava.convert.AbstractConverter;
import org.scijava.convert.ConvertService;
import org.scijava.convert.Converter;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * {@link Converter} for converting {@link List}s to {@link FinalInterval}. This
 * is a chained conversion, and will support {@link List}s of any type that can
 * be converted to {@code long[]}.
 *
 * @author Mark Hiner
 * @author Curtis Rueden
 */
@Plugin(type = Converter.class, priority = Priority.LOW)
public class ConvertListToFinalInterval extends
	ConciseConverter<List, FinalInterval>
{

	@Parameter
	private ConvertService convertService;

	public ConvertListToFinalInterval() {
		super(List.class, FinalInterval.class, null);
	}

	@Override
	public boolean canConvert(final Object src, final Type dest) {
		return super.canConvert(src, dest) && convertService.supports(src,
			long[].class);
	}

	@Override
	public boolean canConvert(final Object src, final Class<?> dest) {
		return super.canConvert(src, dest) && convertService.supports(src,
			long[].class);
	}

	@Override
	public boolean canConvert(final Class<?> src, final Class<?> dest) {
		return super.canConvert(src, dest) && convertService.supports(src,
			long[].class);
	}

	@Override
	protected FinalInterval convert(final List src) {
		final long[] longs = convertService.convert(src, long[].class);
		return new FinalInterval(longs);
	}

	// -- Deprecated API --

	@Override
	@Deprecated
	public boolean canConvert(final Class<?> src, final Type dest) {
		return super.canConvert(src, dest) && convertService.supports(src,
			long[].class);
	}
}
