/*
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

package net.imagej.display.process;

import net.imagej.Dataset;
import net.imagej.display.DataView;
import net.imagej.display.DatasetView;
import net.imagej.display.ImageDisplay;
import net.imagej.display.ImageDisplayService;

import org.scijava.Priority;
import org.scijava.convert.ConvertService;
import org.scijava.module.Module;
import org.scijava.module.ModuleItem;
import org.scijava.module.ModuleService;
import org.scijava.module.process.AbstractSingleInputPreprocessor;
import org.scijava.module.process.PreprocessorPlugin;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Assigns the active image based on the active {@link ImageDisplay} when there
 * is one single unresolved image parameter. Supported image types include
 * {@link ImageDisplay}, {@link DatasetView}, {@link DataView}, {@link Dataset},
 * and any type to which the aforementioned image types can be converted.
 * <p>
 * In the case of more than one image parameter of the same type, the active
 * image is not used and instead the user must select. This behavior is
 * consistent with ImageJ v1.x.
 * </p>
 *
 * @author Curtis Rueden
 */
@Plugin(type = PreprocessorPlugin.class, priority = Priority.VERY_HIGH)
public class ActiveImagePreprocessor extends AbstractSingleInputPreprocessor {

	@Parameter(required = false)
	private ImageDisplayService imageDisplayService;

	@Parameter(required = false)
	private ModuleService moduleService;

	@Parameter(required = false)
	private ConvertService convertService;

	// -- ModulePreprocessor methods --

	@Override
	public void process(final Module m) {
		if (imageDisplayService == null || moduleService == null) return;

		final ImageDisplay imageDisplay = imageDisplayService
			.getActiveImageDisplay();
		if (imageDisplay == null) return;

		final Map<Class<?>, Supplier<?>> types = new LinkedHashMap<>();
		types.put(ImageDisplay.class, () -> imageDisplay);
		types.put(DatasetView.class, () -> imageDisplayService.getActiveDatasetView(
			imageDisplay));
		types.put(DataView.class, () -> imageDisplay.getActiveView());
		types.put(Dataset.class, () -> imageDisplayService.getActiveDataset(
			imageDisplay));

		types.keySet().forEach(type -> fill(m, getSingleInput(m, type), types.get(
			type)));
		types.keySet().forEach(type -> fill(m, getConvertibleSingleInput(m, type),
			types.get(type)));
	}

	// -- Helper methods --

	private String getConvertibleSingleInput(final Module m,
		final Class<?> type)
	{
		if (moduleService == null || convertService == null) return null;
		final ModuleItem<?> item = moduleService.getSingleInput(m, //
			convertService.getCompatibleOutputClasses(type));
		if (item == null || !item.isAutoFill()) return null;
		return item.getName();
	}

	private void fill(final Module module, final String name,
		final Supplier<?> valueSupplier)
	{
		if (name == null) return;
		Object value = valueSupplier.get();
		if (value == null) return;
		final Class<?> type = module.getInfo().getInput(name).getType();
		if (!type.isInstance(value)) value = convertService.convert(value, type);
		if (value == null) return;
		module.setInput(name, value);
		module.resolveInput(name);
	}
}
