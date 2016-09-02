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

package net.imagej.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.List;

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
	public void testAppendColumns() {
		final BoolTable table = createTable();
		final boolean[][] values =
			{
				{ true, true, true, true, false, true, false, true },
				{ true, false, true, false, false, true, false, true },
				{ false, true, true, true, false, true, false, false },
				{ true, false, false, true, false, false, false, true } };

		final String[] headers = { "Header4", "Header5", "Header6", "Header7" };
		final List<BoolColumn> col = table.appendColumns(headers);
		col.get(0).fill(values[0]);
		col.get(1).fill(values[1]);
		col.get(2).fill(values[2]);
		col.get(3).fill(values[3]);

		// Test appending a column
		assertEquals(table.getColumnCount(), 7);
		assertEquals(table.get(3).getHeader(), "Header4");
		assertEquals(table.get(4).getHeader(), "Header5");
		assertEquals(table.get(5).getHeader(), "Header6");
		assertEquals(table.get(6).getHeader(), "Header7");

		checkTableModifiedColumns(table, values, 3, 6);
	}

	@Test
	public void testRemoveColumns() {
		final BoolTable table = createTable();

		final List<BoolColumn> col = table.removeColumns(1, 2);

		// Test removing a column
		for (int q = 0; q < col.size(); q++) {
			for (int i = 0; i < col.get(0).size(); i++) {
				assertEquals(col.get(q).getValue(i), DATA[i][q + 1]);
			}
		}
		assertEquals(table.getColumnCount(), 1);

		checkTableModifiedColumns(table, null, 1, 3);
	}

	@Test
	public void testInsertColumns() {
		final BoolTable table = createTable();
		final boolean[][] values =
			{
				{ true, true, true, true, false, true, false, true },
				{ true, false, true, false, false, true, false, true },
				{ false, true, true, true, false, true, false, false },
				{ true, false, false, true, false, false, false, true } };


		final String[] headers = { "Header4", "Header5", "Header6", "Header7" };
		final List<BoolColumn> col = table.insertColumns(0, headers);
		col.get(0).fill(values[0]);
		col.get(1).fill(values[1]);
		col.get(2).fill(values[2]);
		col.get(3).fill(values[3]);

		// Test appending a column
		assertEquals(table.getColumnCount(), 7);
		assertEquals(table.get(0).getHeader(), "Header4");
		assertEquals(table.get(1).getHeader(), "Header5");
		assertEquals(table.get(2).getHeader(), "Header6");
		assertEquals(table.get(3).getHeader(), "Header7");

		checkTableModifiedColumns(table, values, 0, 3);
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

	@Test
	public void testAppendRows() {
		final BoolTable table = createTable();
		final boolean[][] values =
			{ { true, true, false }, { false, true, false }, { true, false, true },
				{ true, false, false }, { false, false, false } };

		// Test appending a row
		table.appendRows(5);
		assertEquals(table.getRowCount(), 13);
		for (int r = 0; r < values.length; r++) {
			for (int c = 0; c < values[0].length; c++) {
				table.setValue(c, r + 8, values[r][c]);
				assertEquals(table.getValue(c, r + 8), values[r][c]);
			}
		}

		checkTableModifiedRows(table, values, 8, 12);
	}

	@Test
	public void testRemoveRows() {
		final BoolTable table = createTable();
		table.removeRows(4, 3);
		assertEquals(table.getRowCount(), 5);

		checkTableModifiedRows(table, null, 4, 6);
	}

	@Test
	public void testInsertRows() {
		final BoolTable table = createTable();
		final boolean[][] values =
			{ { true, true, false }, { false, true, false }, { true, false, true },
				{ true, false, false }, { false, false, false } };

		table.insertRows(5, 5);

		assertEquals(table.getRowCount(), 13);
		for (int r = 0; r < values.length; r++) {
			for (int c = 0; c < values[0].length; c++) {
				table.setValue(c, r + 5, values[r][c]);
				assertEquals(table.getValue(c, r + 5), values[r][c]);
			}
		}

		checkTableModifiedRows(table, values, 5, 9);
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

	private void checkTableModifiedColumns(final BoolTable table,
		final boolean[][] values, final int startMod, final int endMod)
	{
		for (int r = 0; r < table.getRowCount(); r++) {
			for (int c = 0; c < table.getColumnCount(); c++) {
				if (c >= startMod && c <= endMod && values != null) {
					assertEquals(table.getValue(c, r), values[c - startMod][r]);
				}
				else if (c > endMod && values != null) {
					assertEquals(table.getValue(c, r), DATA[r][c - values.length]);
				}
				else if (c >= startMod && values == null) {
					assertEquals(table.getValue(c, r), DATA[r][c + (endMod - startMod)]);
				}
				else {
					assertEquals(table.getValue(c, r), DATA[r][c]);
				}
			}
		}
	}

	private void checkTableModifiedRows(final BoolTable table,
		final boolean[][] values, final int startMod, final int endMod)
	{
		for (int r = 0; r < table.getRowCount(); r++) {
			for (int c = 0; c < table.getColumnCount(); c++) {
				if (r >= startMod && r <= endMod && values != null) {
					assertEquals(table.getValue(c, r), values[r - startMod][c]);
				}
				else if (r > endMod && values != null) {
					assertEquals(table.getValue(c, r), DATA[r - values.length][c]);
				}
				else if (r >= startMod && values == null) {
					assertEquals(table.getValue(c, r),
						DATA[r + (endMod - startMod + 1)][c]);
				}
				else {
					assertEquals(table.getValue(c, r), DATA[r][c]);
				}
			}
		}
	}

}
