/*
 * #%L
 * ImageJ2 software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2021 ImageJ2 developers.
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

import java.util.Collection;

import org.scijava.convert.ConvertService;
import org.scijava.convert.Converter;
import org.scijava.module.Module;
import org.scijava.module.ModuleItem;
import org.scijava.module.ModuleService;
import org.scijava.module.process.AbstractPreprocessorPlugin;
import org.scijava.plugin.Parameter;

/**
 * Base class for preprocessors that populate single input parameters. The
 * {@link ConvertService} is used so that any single unresolved module that is
 * convertible with the type of this preprocessor can be satisfied. For example,
 * if there is a single unresolved {@code S} parameter, and a {@link Converter}
 * exists from {@code T} to {@code S}, then the parameter can be resolved by
 * this preprocessor.
 * 
 * @author Curtis Rueden
 * @author Mark Hiner hinerm at gmail.com
 */
public abstract class SingleInputPreprocessor<T> extends
	AbstractPreprocessorPlugin
{

	private final Class<T> inputType;

	@Parameter(required = false)
	private ModuleService moduleService;

	@Parameter(required = false)
	private ConvertService convertService;

	public SingleInputPreprocessor(final Class<T> inputType) {
		this.inputType = inputType;
	}

	// -- SingleInputPreprocessor methods --

	/** Gets the single input value to populate. */
	public abstract T getValue();

	// -- ModuleProcessor methods --

	@Override
	public void process(final Module module) {
		// look for single inputs that can be populated
		final ModuleItem<?> singleInput = getSingleInput(module, inputType);
		if (singleInput == null) return;

		// populate the value of the single input
		Object value = getValue();
		if (value == null) return;

		String itemName = singleInput.getName();
		value = convertService.convert(value, singleInput.getType());
		module.setInput(itemName, value);
		module.resolveInput(itemName);
	}

	// -- Helper methods --

	private ModuleItem<?> getSingleInput(final Module module, final Class<?> type) {
		if (moduleService == null || convertService == null) return null;
		// Check the actual class first
		ModuleItem<?> item = moduleService.getSingleInput(module, type);
		if (item == null || !item.isAutoFill()) {
			// No match, so check look for classes that can be converted from the specified type
			final Collection<Class<?>> compatibleClasses = convertService.getCompatibleOutputClasses(type);
			item = moduleService.getSingleInput(module, compatibleClasses);
		}
		if (item == null || !item.isAutoFill()) return null;
		return item;
	}

}
