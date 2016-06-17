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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

/**
 * Tests {@link DefaultBoolTable}.
 * 
 * @author Alison Walter
 */
public class DefaultBoolTableTest {

	private static final String[] HEADERS = { "Header1", "Header2", "Header3" };

	private static final boolean[][] DATA = {
		{ true, true, true },
		{ true, true, false },
		{ true, false, true },
		{ true, false, false },
		{ false, true, true },
		{ false, true, false },
		{ false, false, true },
		{ false, false, false },
	};

	@Test
	public void testStructure() {
		final BoolTable table = createTable();
		assertEquals(3, table.getColumnCount());
		assertEquals(8, table.getRowCount());
		for (final BoolColumn column : table) {
			assertEquals(8, column.size());
		}

		for (int n = 0; n < table.getColumnCount(); n++) {
			assertEquals(table.get(n).getHeader(), HEADERS[n]);
		}

		for (int c = 0; c < table.getColumnCount(); c++) {
			final BoolColumn columnByHeader = table.get(HEADERS[c]);
			final BoolColumn columnByIndex = table.get(c);
			assertSame(columnByHeader, columnByIndex);
			assertEquals(DATA.length, columnByHeader.size());
			for (int r = 0; r < table.getRowCount(); r++) {
				assertEquals(DATA[r][c], table.getValue(c, r));
				assertEquals(DATA[r][c], columnByHeader.getValue(r));
			}
		}
	}

	@Test
	public void testGetColumnType() {
		final BoolTable table = createTable();
		final BoolColumn col = table.get(0);
		assertEquals(col.getType(), Boolean.class);
	}

	@Test
	public void testAppendColumn() {
		final BoolTable table = createTable();
		final boolean[] values =
			{ true, true, true, true, false, true, false, true };

		final BoolColumn col = table.appendColumn("Header4");
		col.fill(values);

		// Test appending a column
		assertEquals(table.getColumnCount(), 4);
		assertEquals(table.get(3).getHeader(), "Header4");

		checkTableModifiedColumn(table, values, 3);
	}

	@Test
	public void testRemoveColumn() {
		final BoolTable table = createTable();
		final BoolColumn col = table.removeColumn(2);

		// Test removing a column
		for (int i = 0; i < col.size(); i++) {
			assertEquals(col.getValue(i), DATA[i][2]);
		}
		assertEquals(table.getColumnCount(), 2);

		checkTableModifiedColumn(table, null, 2);
	}

	@Test
	public void testInsertColumn() {
		final BoolTable table = createTable();
		final boolean[] values =
			{ true, true, true, true, false, true, false, true };

		final BoolColumn col = table.insertColumn(1, "Header4");
		col.fill(values);

		assertEquals(table.getColumnCount(), 4);
		assertEquals(table.get(1).getHeader(), "Header4");

		checkTableModifiedColumn(table, values, 1);
	}

	@Test
	public void testAppendRow() {
		final BoolTable table = createTable();
		final boolean[] values = { true, true, false };

		// Test appending a row
		table.appendRow();
		assertEquals(table.getRowCount(), 9);
		for (int i = 0; i < values.length; i++) {
			table.setValue(i, 8, values[i]);
			assertEquals(table.getValue(i, 8), values[i]);
		}

		checkTableModifiedRow(table, values, 8);
	}

	@Test
	public void testRemoveRow() {
		final BoolTable table = createTable();

		table.removeRow(1);

		assertEquals(table.getRowCount(), 7);
		for (int i = 0; i < table.getColumnCount(); i++) {
			assertEquals(table.getValue(i, 1), DATA[2][i]);
		}

		checkTableModifiedRow(table, null, 1);
	}

	@Test
	public void testInsertRow() {
		final BoolTable table = createTable();
		final boolean[] values = { true, true, false };

		table.insertRow(6);

		assertEquals(table.getRowCount(), 9);
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.setValue(i, 6, values[i]);
		}

		checkTableModifiedRow(table, values, 6);
	}

	// TODO - Add more tests.

	// -- Helper methods --

	private BoolTable createTable() {
		final BoolTable table = new DefaultBoolTable(DATA[0].length, DATA.length);

		for (int c = 0; c < HEADERS.length; c++) {
			table.setColumnHeader(c, HEADERS[c]);
		}

		for (int r = 0; r < DATA.length; r++) {
			for (int c = 0; c < DATA[r].length; c++) {
				table.setValue(c, r, DATA[r][c]);
			}
		}

		return table;
	}

	private void checkTableModifiedColumn(final BoolTable table,
		final boolean[] values, final int mod)
	{
		for (int r = 0; r < table.getRowCount(); r++) {
			for (int c = 0; c < table.getColumnCount(); c++) {
				if ( c == mod && values != null ) {
					assertEquals(table.getValue(c, r), values[r]);
				}
				else if ( c > mod && values != null ) {
					assertEquals(table.getValue(c, r), DATA[r][c - 1]);
				}
				else if ( c >= mod && values == null ) {
					assertEquals(table.getValue(c, r), DATA[r][c + 1]);
				}
				else {
					assertEquals(table.getValue(c, r), DATA[r][c]);
				}
			}
		}
	}

	private void checkTableModifiedRow(final BoolTable table,
		final boolean[] values, final int mod)
	{
		for (int r = 0; r < table.getRowCount(); r++) {
			for (int c = 0; c < table.getColumnCount(); c++) {
				if ( r == mod && values != null ) {
					assertEquals(table.getValue(c, r), values[c]);
				}
				else if ( r > mod && values != null) {
					assertEquals(table.getValue(c, r), DATA[r-1][c]);
				}
				else if ( r >= mod && values == null ) {
					assertEquals(table.getValue(c, r), DATA[r+1][c]);
				}
				else {
					assertEquals(table.getValue(c, r), DATA[r][c]);
				}
			}
		}
	}

}
