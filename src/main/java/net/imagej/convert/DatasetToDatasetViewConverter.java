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

import net.imagej.Dataset;
import net.imagej.display.DatasetView;
import net.imagej.display.ImageDisplayService;

import org.scijava.Priority;
import org.scijava.convert.AbstractConverter;
import org.scijava.convert.Converter;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Simple {@link Converter} for wrapping {@link Dataset}s into a
 * {@link DatasetView} of it.
 *
 * @author Gabriel Selzer
 */
@Plugin(type = Converter.class, priority = Priority.NORMAL + 1)
public class DatasetToDatasetViewConverter extends
	AbstractConverter<Dataset, DatasetView>
{

	@Parameter
	private ImageDisplayService imageDisplayService;

	@Override
	public <T> T convert(final Object o, final Class<T> aClass) {
		if (!(o instanceof Dataset)) {
			throw new IllegalArgumentException("Object is not a Dataset");
		}
		final Dataset ds = (Dataset) o;
		final DatasetView view = imageDisplayService.createDatasetView(ds);
		if (!aClass.isInstance(view)) {
			throw new IllegalArgumentException("Class " + aClass.getName() +
				" is incompatible with converted DatasetView.");
		}
		@SuppressWarnings("unchecked")
		final T result = (T) view;
		return result;
	}

	@Override
	public Class<Dataset> getInputType() {
		return Dataset.class;
	}

	@Override
	public Class<DatasetView> getOutputType() {
		return DatasetView.class;
	}
}
