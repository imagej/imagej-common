/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2017 Board of Regents of the University of
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
 * Tests {@link DefaultIntTable}.
 *
 * @author Alison Walter
 */
public class DefaultIntTableTest {

	private static final String[] HEADERS = { "Header1", "Header2", "Header3",
		"Header4", "Header5", "Header6" };

	private static final int[][] DATA = {
		{ 127, -45, 605, -123440804, -4082, 57823 },
		{ 0, 0, 9, 12, 856, 1036 },
		{ 17, 77, -684325, 894, -3246, 423 },
		{ -3, -5234, 97, 93726, 672, -2 },
		{ 4, 6, -22222222, 56234, -934270, -1938475430 },
		{ 2147483647, -104, 867, -8, 386443263, 1248 },
		{ 12, -2147483648, 456, 4652, -17, 95 },
		{ 9275, -676, 7, 134, -32176368, 759 },
		{ 184, 56, 104920256, 1436437635, -435, 1 },
		{ 67, 3, -9, 94754, 4, -287934657 },
		{ 48, -356, -748, -93784, 5879, 5 },
		{ 289, 546453765, 0, 0, -6456, -23455 },
		{ 768, -1411556, 7, 2356, 7925, 7468 },
		{ -45, 25367, 546, 6757, -3, 1645 },
		{ 1, 6, -2562345, -23565584, -35815, 956 },
	};

	@Test
	public void testStructure() {
		final IntTable table = createTable();
		// Check table size
		assertEquals(6, table.getColumnCount());
		assertEquals(15, table.getRowCount());
		for (final IntColumn column : table) {
			assertEquals(15, column.size());
		}

		// Test headers
		for (int n = 0; n < table.getColumnCount(); n++) {
			assertEquals(table.getColumnHeader(n), HEADERS[n]);
		}

		// Test getting columns
		for (int c = 0; c < table.getColumnCount(); c++) {
			final IntColumn columnByHeader = table.get(HEADERS[c]);
			final IntColumn columnByIndex = table.get(c);
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
		final IntTable table = createTable();
		final IntColumn col = table.get(0);
		assertEquals(col.getType(), Integer.class);
	}

	@Test
	public void testAppendColumn() {
		final IntTable table = createTable();
		final int[] values =
			{ 30, 3109842, 28, 25, -432579, 22, -12, 0, 54235423, -7858345, -34, -3,
			-35648443, 43512356, 999 };

		final IntColumn col = table.appendColumn("Header7");
		col.fill(values);

		// Test appending a column
		assertEquals(table.getColumnCount(), 7);
		assertEquals(table.get(6).getHeader(), "Header7");

		checkTableModifiedColumn(table, values, 6);
	}

	@Test
	public void testRemoveColumn() {
		final IntTable table = createTable();

		// Test removing a column
		final IntColumn col = table.removeColumn(5);

		for (int i = 0; i < col.size(); i++) {
			assertEquals(col.getValue(i), DATA[i][5]);
		}
		assertEquals(table.getColumnCount(), 5);

		checkTableModifiedColumn(table, null, 5);
	}

	@Test
	public void testInsertColumn() {
		final IntTable table = createTable();
		final int[] values =
			{ 30, 3109842, 28, 25, -432579, 22, -12, 0, 54235423, -7858345, -34, -3,
			-35648443, 43512356, 999 };

		final IntColumn col = table.insertColumn(3, "Header7");
		col.fill(values);

		assertEquals(table.getColumnCount(), 7);
		assertEquals(table.get(3).getHeader(), "Header7");

		checkTableModifiedColumn(table, values, 3);
	}

	@Test
	public void testAppendColumns() {
		final IntTable table = createTable();
		final int[][] values =
			{
				{ 30, 3109842, 28, 25, -432579, 22, -12, 0, 54235423, -7858345, -34, -3,
					-35648443, 43512356, 999 },
				{ 9, 310, -11111, -979324, -4092345, 1, -5, 0, 7, -13, -4903285, 42032,
					-840, 974, 1 } };

		final String[] headers = { "Header7", "Header8" };
		final List<IntColumn> col = table.appendColumns(headers);
		col.get(0).fill(values[0]);
		col.get(1).fill(values[1]);

		// Test appending a column
		assertEquals(table.getColumnCount(), 8);
		assertEquals(table.get(6).getHeader(), "Header7");
		assertEquals(table.get(7).getHeader(), "Header8");

		checkTableModifiedColumns(table, values, 6, 7);
	}

	@Test
	public void testRemoveColumns() {
		final IntTable table = createTable();

		final List<IntColumn> col = table.removeColumns(2, 2);

		// Test removing a column
		for (int q = 0; q < col.size(); q++) {
			for (int i = 0; i < col.get(0).size(); i++) {
				assertEquals(col.get(q).getValue(i), DATA[i][q + 2]);
			}
		}
		assertEquals(table.getColumnCount(), 4);

		checkTableModifiedColumns(table, null, 2, 4);
	}

	@Test
	public void testInsertColumns() {
		final IntTable table = createTable();
		final int[][] values =
			{
				{ 30, 3109842, 28, 25, -432579, 22, -12, 0, 54235423, -7858345, -34, -3,
					-35648443, 43512356, 999 },
				{ 9, 310, -11111, -979324, -4092345, 1, -5, 0, 7, -13, -4903285, 42032,
					-840, 974, 1 } };

		final String[] headers = { "Header7", "Header8" };
		final List<IntColumn> col = table.insertColumns(4, headers);
		col.get(0).fill(values[0]);
		col.get(1).fill(values[1]);

		// Test appending a column
		assertEquals(table.getColumnCount(), 8);
		assertEquals(table.get(4).getHeader(), "Header7");
		assertEquals(table.get(5).getHeader(), "Header8");

		checkTableModifiedColumns(table, values, 4, 5);
	}

	@Test
	public void testAppendRow() {
		final IntTable table = createTable();
		final int[] values = { 179, 43148, -36, 1, 6, -356 };

		// Test appending a row
		table.appendRow();
		assertEquals(table.getRowCount(), 16);
		for (int i = 0; i < values.length; i++) {
			table.setValue(i, 15, values[i]);
			assertEquals(table.getValue(i, 15), values[i]);
		}

		checkTableModifiedRow(table, values, 15);
	}

	@Test
	public void testRemoveRow() {
		final IntTable table = createTable();

		// Test removing a row
		table.removeRow(10);
		assertEquals(table.getRowCount(), 14);
		for (int i = 0; i < table.getColumnCount(); i++) {
			assertEquals(table.getValue(i, 10), DATA[11][i]);
		}

		checkTableModifiedRow(table, null, 10);
	}

	@Test
	public void testInsertRow() {
		final IntTable table = createTable();
		final int[] values = { 179, 43148, -36, 1, 6, -356 };

		table.insertRow(13);

		assertEquals(table.getRowCount(), 16);
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.setValue(i, 13, values[i]);
		}

		checkTableModifiedRow(table, values, 13);
	}

	@Test
	public void testAppendRows() {
		final IntTable table = createTable();
		final int[][] values =
			{ { 179, 43148, -36, 1, 6, -356 }, { 1, 438, -6, 145, -632, -6123 },
				{ -419, 4, -0, -41, 43, 134 } };

		// Test appending a row
		table.appendRows(3);
		assertEquals(table.getRowCount(), 18);
		for (int r = 0; r < values.length; r++) {
			for (int c = 0; c < values[0].length; c++) {
				table.setValue(c, r + 15, values[r][c]);
				assertEquals(table.getValue(c, r + 15), values[r][c]);
			}
		}

		checkTableModifiedRows(table, values, 15, 17);
	}

	@Test
	public void testRemoveRows() {
		final IntTable table = createTable();
		table.removeRows(9, 2);
		assertEquals(table.getRowCount(), 13);

		checkTableModifiedRows(table, null, 9, 10);
	}

	@Test
	public void testInsertRows() {
		final IntTable table = createTable();
		final int[][] values =
			{ { 179, 43148, -36, 1, 6, -356 }, { 1, 438, -6, 145, -632, -6123 },
				{ -419, 4, -0, -41, 43, 134 } };

		table.insertRows(2, 3);

		assertEquals(table.getRowCount(), 18);
		for (int r = 0; r < values.length; r++) {
			for (int c = 0; c < values[0].length; c++) {
				table.setValue(c, r + 2, values[r][c]);
				assertEquals(table.getValue(c, r + 2), values[r][c]);
			}
		}

		checkTableModifiedRows(table, values, 2, 4);
	}

	// TODO - Add more tests.

	// -- Helper methods --

	private IntTable createTable() {
		final IntTable table = new DefaultIntTable(DATA[0].length, DATA.length);

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

	private void checkTableModifiedColumn(final IntTable table,
		final int[] values, final int mod)
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

	private void checkTableModifiedRow(final IntTable table, final int[] values,
		final int mod)
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

	private void checkTableModifiedColumns(final IntTable table,
		final int[][] values, final int startMod, final int endMod)
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

	private void checkTableModifiedRows(final IntTable table,
		final int[][] values, final int startMod, final int endMod)
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
