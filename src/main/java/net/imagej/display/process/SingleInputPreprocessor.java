/*
 * #%L
 * ImageJ2 software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2024 ImageJ2 developers.
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
 * A base class for preprocessors that populate single input parameters. The
 * {@link ConvertService} is used so that any single unresolved module that is
 * convertible with the type of this preprocessor can be satisfied. For example,
 * if there is a single unresolved {@code S} parameter, and a {@link Converter}
 * exists from {@code T} to {@code S}, then the parameter can be resolved by
 * this preprocessor.
 * <p>
 * NB: There is another base class,
 * {@link org.scijava.module.process.AbstractSingleInputPreprocessor}, which
 * alternately can be used to create module preprocessor plugins for filling
 * single inputs of particular types. The differences between the two are:
 * </p>
 * <ul>
 * <li>The SciJava {@code AbstractSingleInputPreprocessor} is less structured,
 * with only a protected helper method {@code getSingleInput(Module, Class)} to
 * look up whether a particular module instance has a single input parameter of
 * the given type; implementations may look up any number of single inputs of
 * whatever types, but must implement {@code process(Module)} directly in
 * whatever way they see fit. Conversely, this {@code SingleInputPreprocessor}
 * is more structured, with a generic parameter indicating the type of the
 * single input that will be handled, and a new protected {@code getValue()}
 * method returning the single input value to inject as appropriate.</li>
 * <li>The SciJava {@code AbstractSingleInputPreprocessor} does <em>not</em>
 * reason about types beyond type assignability, whereas this
 * {@code SingleInputPreprocessor} checks for single inputs of <em>all types
 * convertible from the requested type</em>. For example, a module requiring a
 * single {@code Log} input would be filled by a {@code SingleTreePreprocessor}
 * plugin extending this {@code SingleInputPreprocessor}, when a
 * {@code TreeToLogConverter} plugin is present, because the machinery of this
 * class would notice that a single {@code Log} is required, that a {@code Tree}
 * can be provided, and that the {@code Tree} can be converted into the needed
 * {@code Log}.</li>
 * </ul>
 * <p>
 * Therefore: it is recommended to use this class when:
 * <ol>
 * <li>your implementing class handles one specific type including subtypes
 * (i.e. not multiple heterogeneous/unrelated types); and</li>
 * <li>automatic conversion handling is desirable for your handled type.</li>
 * </ol>
 * <p>
 * Whereas if you need to handle multiple input types, and/or do not necessarily
 * want type conversion into the destination single input type, it is
 * recommended to use {@code AbstractSingleInputPreprocessor} instead.
 * </p>
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
