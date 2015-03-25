/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2015 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
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

import org.scijava.module.Module;
import org.scijava.module.ModuleItem;
import org.scijava.module.ModuleService;
import org.scijava.module.process.AbstractPreprocessorPlugin;
import org.scijava.plugin.Parameter;

/**
 * Base class for preprocessors that populate single input parameters.
 * 
 * @author Curtis Rueden
 */
public abstract class SingleInputPreprocessor<T> extends
	AbstractPreprocessorPlugin
{

	private final Class<T> inputType;

	@Parameter(required = false)
	private ModuleService moduleService;

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
		final String singleInput = getSingleInput(module, inputType);
		if (singleInput == null) return;

		// populate the value of the single input
		final Object value = getValue();
		if (value == null) return;
		module.setInput(singleInput, value);
		module.setResolved(singleInput, true);
	}

	// -- Helper methods --

	private String getSingleInput(final Module module, final Class<?> type) {
		if (moduleService == null) return null;
		final ModuleItem<?> item = moduleService.getSingleInput(module, type);
		if (item == null || !item.isAutoFill()) return null;
		return item.getName();
	}

}
