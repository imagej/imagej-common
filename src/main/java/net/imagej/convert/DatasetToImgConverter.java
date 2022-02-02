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

import net.imagej.Dataset;
import net.imglib2.img.Img;

import org.scijava.Priority;
import org.scijava.convert.AbstractConverter;
import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;
import org.scijava.util.Types;

/**
 * Simple {@link Converter} for unwrapping {@link Dataset}s to their underlying
 * {@link Img}.
 *
 * @author Mark Hiner
 */
@SuppressWarnings("rawtypes")
@Plugin(type = Converter.class, priority = Priority.NORMAL + 1)
public class DatasetToImgConverter extends AbstractConverter<Dataset, Img> {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(final Object src, final Class<T> dest) {
		final Dataset data = (Dataset) src;
		return (T) data.getImgPlus().getImg();
	}

	@Override
	public Class<Img> getOutputType() {
		return Img.class;
	}

	@Override
	public Class<Dataset> getInputType() {
		return Dataset.class;
	}

	@Override
	public boolean canConvert(final Object src, final Type dest) {
		if (src == null)
			return false;
		final Class<?> destClass = Types.raw(dest);
		return canConvert(src, destClass);
	}

	@Override
	public boolean canConvert(final Object src, final Class<?> dest) {
		if (src == null)
			return false;
		Class<?> srcClass = src.getClass();
		if (Dataset.class.isAssignableFrom(srcClass)) {
			final Dataset data = (Dataset) src;
			srcClass = data.getImgPlus().getImg().getClass();
			return Types.isAssignable(srcClass, dest);
		}

		return false;
	}
}
