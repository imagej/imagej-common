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

package net.imagej.table;

import org.scijava.display.AbstractDisplay;
import org.scijava.display.Display;
import org.scijava.plugin.Plugin;

/**
 * Default display for {@link Table}s, including {@link ResultsTable}s.
 * 
 * @author Curtis Rueden
 */
@Plugin(type = Display.class)
public class DefaultTableDisplay extends AbstractDisplay<Table<?, ?>> implements
	TableDisplay
{

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public DefaultTableDisplay() {
		super((Class) Table.class);
	}

	// -- Display methods --

	@Override
	public boolean canDisplay(final Class<?> c) {
		if (c == double[].class || c == double[][].class) return true;
		return super.canDisplay(c);
	}

	@Override
	public void display(final Object o) {
		// wrap 1D array as results table
		if (o instanceof double[]) {
			display(wrapArrayAsTable(new double[][] { (double[]) o }));
			return;
		}
		// wrap 2D array as results table
		if (o instanceof double[][]) {
			display(wrapArrayAsTable((double[][]) o));
			return;
		}

		super.display(o);
	}

	@Override
	public boolean isDisplaying(final Object o) {
		if (super.isDisplaying(o)) return true;

		// check for wrapped arrays
		if (o instanceof double[]) {
			arrayEqualsTable(new double[][] {(double[]) o});
		}
		if (o instanceof double[][]) {
			arrayEqualsTable((double[][]) o);
		}

		return false;
	}

	// -- Helper methods --

	private ResultsTable wrapArrayAsTable(final double[][] array) {
		final ResultsTable table = new DefaultResultsTable();
		int rowCount = 0;
		for (int d = 0; d < array.length; d++) {
			final DoubleColumn column = new DoubleColumn();
			column.setArray(array[d]);
			table.add(column);
			if (rowCount < array[d].length) rowCount = array[d].length;
		}
		table.setRowCount(rowCount);
		return table;
	}

	private boolean arrayEqualsTable(final double[][] array) {
		for (final Table<?, ?> table : this) {
			if (!(table instanceof ResultsTable)) continue;
			final ResultsTable resultsTable = (ResultsTable) table;
			if (array.length != resultsTable.getColumnCount()) continue;
			boolean equal = true;
			for (int c = 0; c < array.length; c++) {
				if (array[c] != resultsTable.get(c).getArray()) {
					equal = false;
					break;
				}
			}
			return equal;
		}
		return false;
	}

}
