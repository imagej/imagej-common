/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2016 Board of Regents of the University of
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

package net.imagej.table.process;

import java.util.ArrayList;

import net.imagej.table.DefaultColumn;
import net.imagej.table.DefaultGenericTable;
import net.imagej.table.GenericTable;

import org.scijava.Priority;
import org.scijava.module.Module;
import org.scijava.module.ModuleItem;
import org.scijava.module.process.AbstractPostprocessorPlugin;
import org.scijava.module.process.PostprocessorPlugin;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;
import org.scijava.util.ClassUtils;

/**
 * A postprocessor which aggregates simple output values into a single table,
 * for a nicer UI experience.
 * 
 * @author Curtis Rueden
 */
@Plugin(type = PostprocessorPlugin.class,
	priority = Priority.VERY_LOW_PRIORITY + 1)
public class ResultsPostprocessor extends AbstractPostprocessorPlugin {

	@Parameter(required = false)
	private UIService ui;

	// -- ModuleProcessor methods --

	@Override
	public void process(final Module module) {
		if (ui == null) {
			// no UIService available for displaying results
			return;
		}

		// filter the compatible outputs (simple types: number, boolean, text)
		final ArrayList<ModuleItem<?>> outputs = new ArrayList<>();
		module.getInfo().outputs().forEach(output -> {
			final String name = output.getName();
			if (module.isOutputResolved(name)) return;
			if (module.getOutput(name) == null) return;
			if (!isSimple(module, output)) return;
			outputs.add(output);
		});

		if (outputs.isEmpty()) return; // no compatible outputs
		if (outputs.size() == 1 && ClassUtils.isText(outputs.get(0).getType())) {
			// sole compatible output is a string; let the TextDisplay handle it
			return;
		}

		// create a table to house the output values
		final GenericTable outputTable = new DefaultGenericTable();
		final DefaultColumn<String> names = //
			new DefaultColumn<>(String.class, "Name");
		final DefaultColumn<Object> values = //
			new DefaultColumn<>(Object.class, "Value");

		// populate the columns
		for (final ModuleItem<?> output : outputs) {
			final String name = output.getName();
			names.addValue(name);
			values.addValue(module.getOutput(name));
			module.resolveOutput(name);
		}

		// show the table
		outputTable.add(names);
		outputTable.add(values);
		final String title = module.getInfo().getTitle();
		ui.show(title, outputTable);
	}

	// -- Helper methods --

	private boolean isSimple(final Module m, final ModuleItem<?> item) {
		final Class<?> type = item.getType();
		return isSimpleType(type) || //
			// NB: The output is typed on Object -- maybe the default result output.
			// In this case, let's decide based on the actual value rather than type.
			type == Object.class && isSimpleValue(item.getValue(m));
	}

	private boolean isSimpleType(final Class<?> type) {
		return ClassUtils.isText(type) || //
			ClassUtils.isNumber(type) || //
			ClassUtils.isBoolean(type);
	}

	private boolean isSimpleValue(final Object o) {
		return o != null && isSimpleType(o.getClass());
	}
}
