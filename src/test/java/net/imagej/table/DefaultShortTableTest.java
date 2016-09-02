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
 * Tests {@link DefaultShortTable}.
 *
 * @author Alison Walter
 */
public class DefaultShortTableTest {

	private static final String[] HEADERS = { "Header1", "Header2", "Header3",
		"Header4" };

	private static final short[][] DATA = {
		{ -32768, 0, 32767, 12 },
		{ 3024, -31, 34, 21, },
		{ 1, 2, 3, 4 },
		{ 121, -3987, 7353, 9139 },
		{ -31987, 5987, 12, -33 },
	};

	@Test
	public void testStructure() {
		final ShortTable table = createTable();
		// Check table size
		assertEquals(4, table.getColumnCount());
		assertEquals(5, table.getRowCount());
		for (final ShortColumn column : table) {
			assertEquals(5, column.size());
		}

		// Test headers
		for (int n = 0; n < table.getColumnCount(); n++) {
			assertEquals(table.getColumnHeader(n), HEADERS[n]);
		}

		// Test getting columns
		for (int c = 0; c < table.getColumnCount(); c++) {
			final ShortColumn columnByHeader = table.get(HEADERS[c]);
			final ShortColumn columnByIndex = table.get(c);
			assertSame(columnByHeader, columnByIndex);
			assertEquals(DATA.length, columnByHeader.size());
			// Test columns have expected row values
			for (int r = 0; r < table.getRowCount(); r++) {
				assertEquals(DATA[r][c], table.getValue(c, r));
				assertEquals(DATA[r][c], columnByHeader.getValue(r));
			}
		}
	}

	@Test
	public void testGetColumnType() {
		final ShortTable table = createTable();
		final ShortColumn col = table.get(0);
		assertEquals(col.getType(), Short.class);
	}

	@Test
	public void testAppendColumn() {
		final ShortTable table = createTable();
		final short[] values = { -11, 32000, 9798, -18687, 97 };

		final ShortColumn col = table.appendColumn("Header5");
		col.fill(values);

		// Test appending a column
		assertEquals(table.getColumnCount(), 5);
		assertEquals(table.get(4).getHeader(), "Header5");

		checkTableModifiedColumn(table, values, 4);
	}

	@Test
	public void testInsertColumn() {
		final ShortTable table = createTable();
		final short[] values = { -11, 32000, 9798, -18687, 97 };

		final ShortColumn col = table.insertColumn(2, "Header5");
		col.fill(values);

		assertEquals(table.getColumnCount(), 5);
		assertEquals(table.get(2).getHeader(), "Header5");

		checkTableModifiedColumn(table, values, 2);
	}

	@Test
	public void testRemoveColumn() {
		final ShortTable table = createTable();

		final ShortColumn col2 = table.removeColumn(2);

		// Test removing a column
		for (int i = 0; i < col2.size(); i++) {
			assertEquals(col2.getValue(i), DATA[i][2]);
		}
		assertEquals(table.getColumnCount(), 3);

		checkTableModifiedColumn(table, null, 2);
	}

	@Test
	public void testAppendColumns() {
		final ShortTable table = createTable();
		final short[][] values =
			{
				{ -11, 32000, 9798, -18687, 97 },
				{ 19487, 3, 786, 12984, 113 },
				{ -1, 8, -234, -9, 10 } };

		final String[] headers = { "Header5", "Header6", "Header7" };
		final List<ShortColumn> col = table.appendColumns(headers);
		col.get(0).fill(values[0]);
		col.get(1).fill(values[1]);
		col.get(2).fill(values[2]);

		// Test appending a column
		assertEquals(table.getColumnCount(), 7);
		assertEquals(table.get(4).getHeader(), "Header5");
		assertEquals(table.get(5).getHeader(), "Header6");
		assertEquals(table.get(6).getHeader(), "Header7");

		checkTableModifiedColumns(table, values, 4, 7);
	}

	@Test
	public void testRemoveColumns() {
		final ShortTable table = createTable();

		final List<ShortColumn> col = table.removeColumns(1, 3);

		// Test removing a column
		for (int q = 0; q < col.size(); q++) {
			for (int i = 0; i < col.get(0).size(); i++) {
				assertEquals(col.get(q).getValue(i), DATA[i][q + 1]);
			}
		}
		assertEquals(table.getColumnCount(), 1);

		checkTableModifiedColumns(table, null, 1, 4);
	}

	@Test
	public void testInsertColumns() {
		final ShortTable table = createTable();
		final short[][] values =
			{
				{ -11, 32000, 9798, -18687, 97 },
				{ 19487, 3, 786, 12984, 113 },
				{ -1, 8, -234, -9, 10 } };

		final String[] headers = { "Header5", "Header6", "Header7" };
		final List<ShortColumn> col = table.insertColumns(3, headers);
		col.get(0).fill(values[0]);
		col.get(1).fill(values[1]);
		col.get(2).fill(values[2]);

		// Test appending a column
		assertEquals(table.getColumnCount(), 7);
		assertEquals(table.get(3).getHeader(), "Header5");
		assertEquals(table.get(4).getHeader(), "Header6");
		assertEquals(table.get(5).getHeader(), "Header7");

		checkTableModifiedColumns(table, values, 3, 5);
	}

	@Test
	public void testAppendRow() {
		final ShortTable table = createTable();
		final short[] values = { 7911, 937, -1508, -8 };

		// Test appending a row
		table.appendRow();
		assertEquals(table.getRowCount(), 6);
		for (int i = 0; i < values.length; i++) {
			table.setValue(i, 5, values[i]);
			assertEquals(table.getValue(i, 5), values[i]);
		}

		checkTableModifiedRow(table, values, 5);
	}

	@Test
	public void testRemoveRow() {
		final ShortTable table = createTable();

		// Test removing a row
		table.removeRow(2);

		assertEquals(table.getRowCount(), 4);
		for (int i = 0; i < table.getColumnCount(); i++) {
			assertEquals(table.getValue(i, 2), DATA[3][i]);
		}

		checkTableModifiedRow(table, null, 2);
	}

	@Test
	public void testInsertRow() {
		final ShortTable table = createTable();
		final short[] values = { 7911, 937, -1508, -8 };

		table.insertRow(3);

		assertEquals(table.getRowCount(), 6);
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.setValue(i, 3, values[i]);
		}

		checkTableModifiedRow(table, values, 3);
	}

	@Test
	public void testAppendRows() {
		final ShortTable table = createTable();
		final short[][] values =
			{ { 7911, 937, -1508, -8 }, { -1212, 16, -1, -10 } };

		// Test appending a row
		table.appendRows(2);
		assertEquals(table.getRowCount(), 7);
		for (int r = 0; r < values.length; r++) {
			for (int c = 0; c < values[0].length; c++) {
				table.setValue(c, r + 5, values[r][c]);
				assertEquals(table.getValue(c, r + 5), values[r][c]);
			}
		}

		checkTableModifiedRows(table, values, 5, 6);
	}

	@Test
	public void testRemoveRows() {
		final ShortTable table = createTable();
		table.removeRows(2, 2);
		assertEquals(table.getRowCount(), 3);

		checkTableModifiedRows(table, null, 2, 3);
	}

	@Test
	public void testInsertRows() {
		final ShortTable table = createTable();
		final short[][] values =
			{ { 7911, 937, -1508, -8 }, { -1212, 16, -1, -10 } };

		table.insertRows(3, 2);

		assertEquals(table.getRowCount(), 7);
		for (int r = 0; r < values.length; r++) {
			for (int c = 0; c < values[0].length; c++) {
				table.setValue(c, r + 3, values[r][c]);
				assertEquals(table.getValue(c, r + 3), values[r][c]);
			}
		}

		checkTableModifiedRows(table, values, 3, 4);
	}

	// TODO - Add more tests.

	// -- Helper methods --

	private ShortTable createTable() {
		final ShortTable table = new DefaultShortTable(DATA[0].length, DATA.length);

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

	private void checkTableModifiedColumn(final ShortTable table,
		final short[] values, final int mod)
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

	private void checkTableModifiedRow(final ShortTable table,
		final short[] values, final int mod)
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

	private void checkTableModifiedColumns(final ShortTable table,
		final short[][] values, final int startMod, final int endMod)
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

	private void checkTableModifiedRows(final ShortTable table,
		final short[][] values, final int startMod, final int endMod)
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
