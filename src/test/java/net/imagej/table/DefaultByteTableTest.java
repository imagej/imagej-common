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
 * Tests {@link DefaultByteTable}.
 *
 * @author Alison Walter
 */
public class DefaultByteTableTest {

	private static final String[] HEADERS = { "Header1", "Header2" };

	private static final byte[][] DATA = {
		{ 127, -45 },
		{ -128, 0 },
		{ 64, 17 },
		{ -32, 6 },
		{ 4, 98 },
		{ -74, -104 },
		{ 12, 89 },
	};

	@Test
	public void testStructure() {
		final ByteTable table = createTable();
		assertEquals(2, table.getColumnCount());
		assertEquals(7, table.getRowCount());
		for (final ByteColumn column : table) {
			assertEquals(7, column.size());
		}

		for (int n = 0; n < table.getColumnCount(); n++) {
			assertEquals(table.get(n).getHeader(), HEADERS[n]);
		}

		for (int c = 0; c < table.getColumnCount(); c++) {
			final ByteColumn columnByHeader = table.get(HEADERS[c]);
			final ByteColumn columnByIndex = table.get(c);
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
		final ByteTable table = createTable();
		final ByteColumn col = table.get(0);
		assertEquals(col.getType(), Byte.class);
	}

	@Test
	public void testAppendColumn() {
		final ByteTable table = createTable();
		final byte[] values = { 17, 23, -12, 0, -93, -7, 127 };

		final ByteColumn col = table.appendColumn("Header3");
		col.fill(values);

		// Test appending a column
		assertEquals(table.getColumnCount(), 3);
		assertEquals(table.get(2).getHeader(), "Header3");

		checkTableModifiedColumn(table, values, 2);
	}

	@Test
	public void testRemoveColumn() {
		final ByteTable table = createTable();
		final ByteColumn col = table.removeColumn(1);

		// Test removing a column
		for (int i = 0; i < col.size(); i++) {
			assertEquals(col.getValue(i), DATA[i][1]);
		}
		assertEquals(table.getColumnCount(), 1);

		checkTableModifiedColumn(table, null, 1);
	}

	@Test
	public void testInsertColumn() {
		final ByteTable table = createTable();
		final byte[] values = { 17, 23, -12, 0, -93, -7, 127 };

		final ByteColumn col = table.insertColumn(0, "Header3");
		col.fill(values);

		assertEquals(table.getColumnCount(), 3);
		assertEquals(table.get(0).getHeader(), "Header3");

		checkTableModifiedColumn(table, values, 0);
	}

	@Test
	public void testAppendColumns() {
		final ByteTable table = createTable();
		final byte[][] values =
			{
				{ 17, 23, -12, 0, -93, -7, 127 },
				{ -100, 54, 93, -2, 111, -86, -74 },
				{ -12, 120, 6, 8, -4, -121, 13 } };

		final String[] headers = { "Header3", "Header4", "Header5" };
		final List<ByteColumn> col = table.appendColumns(headers);
		col.get(0).fill(values[0]);
		col.get(1).fill(values[1]);
		col.get(2).fill(values[2]);

		// Test appending a column
		assertEquals(table.getColumnCount(), 5);
		assertEquals(table.get(2).getHeader(), "Header3");
		assertEquals(table.get(3).getHeader(), "Header4");
		assertEquals(table.get(4).getHeader(), "Header5");

		checkTableModifiedColumns(table, values, 2, 4);
	}

	@Test
	public void testRemoveColumns() {
		final ByteTable table = createTable();

		final List<ByteColumn> col = table.removeColumns(0, 2);

		// Test removing a column
		for (int q = 0; q < col.size(); q++) {
			for (int i = 0; i < col.get(0).size(); i++) {
				assertEquals(col.get(q).getValue(i), DATA[i][q]);
			}
		}
		assertEquals(table.getColumnCount(), 0);

		checkTableModifiedColumns(table, null, 0, 1);
	}

	@Test
	public void testInsertColumns() {
		final ByteTable table = createTable();
		final byte[][] values =
			{
				{ 17, 23, -12, 0, -93, -7, 127 },
				{ -100, 54, 93, -2, 111, -86, -74 },
				{ -12, 120, 6, 8, -4, -121, 13 } };

		final String[] headers = { "Header3", "Header4", "Header5" };
		final List<ByteColumn> col = table.insertColumns(1, headers);
		col.get(0).fill(values[0]);
		col.get(1).fill(values[1]);
		col.get(2).fill(values[2]);

		assertEquals(table.getColumnCount(), 5);
		assertEquals(table.get(1).getHeader(), "Header3");
		assertEquals(table.get(2).getHeader(), "Header4");
		assertEquals(table.get(3).getHeader(), "Header5");

		checkTableModifiedColumns(table, values, 1, 3);
	}

	@Test
	public void testAppendRow() {
		final ByteTable table = createTable();
		final byte[] values = { 79, 8 };

		// Test appending a row
		table.appendRow();
		assertEquals(table.getRowCount(), 8);
		for (int i = 0; i < values.length; i++) {
			table.setValue(i, 7, values[i]);
			assertEquals(table.getValue(i, 7), values[i]);
		}

		checkTableModifiedRow(table, values, 7);
	}

	@Test
	public void testRemoveRow() {
		final ByteTable table = createTable();

		// Test removing a row
		table.removeRow(2);

		assertEquals(table.getRowCount(), 6);
		for (int i = 0; i < table.getColumnCount(); i++) {
			assertEquals(table.getValue(i, 2), DATA[3][i]);
		}

		checkTableModifiedRow(table, null, 2);
	}

	@Test
	public void testInsertRow() {
		final ByteTable table = createTable();
		final byte[] values = { 79, 8 };

		table.insertRow(5);

		assertEquals(table.getRowCount(), 8);
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.setValue(i, 5, values[i]);
		}

		checkTableModifiedRow(table, values, 5);
	}

	@Test
	public void testAppendRows() {
		final ByteTable table = createTable();
		final byte[][] values =
			{ { 79, 8 }, { 100, -12 }, { 54, 36 }, { -100, -86 }, { -43, 60 },
				{ 92, -122 } };

		// Test appending a row
		table.appendRows(6);
		assertEquals(table.getRowCount(), 13);
		for (int r = 0; r < values.length; r++) {
			for (int c = 0; c < values[0].length; c++) {
				table.setValue(c, r + 7, values[r][c]);
				assertEquals(table.getValue(c, r + 7), values[r][c]);
			}
		}

		checkTableModifiedRows(table, values, 7, 12);
	}

	@Test
	public void testRemoveRows() {
		final ByteTable table = createTable();
		table.removeRows(2, 3);
		assertEquals(table.getRowCount(), 4);

		checkTableModifiedRows(table, null, 2, 4);
	}

	@Test
	public void testInsertRows() {
		final ByteTable table = createTable();
		final byte[][] values =
			{ { 79, 8 }, { 100, -12 }, { 54, 36 }, { -100, -86 }, { -43, 60 },
				{ 92, -122 } };

		table.insertRows(4, 6);

		assertEquals(table.getRowCount(), 13);
		for (int r = 0; r < values.length; r++) {
			for (int c = 0; c < values[0].length; c++) {
				table.setValue(c, r + 4, values[r][c]);
				assertEquals(table.getValue(c, r + 4), values[r][c]);
			}
		}

		checkTableModifiedRows(table, values, 4, 9);
	}

	// TODO - Add more tests.

	// -- Helper methods -- 

	private ByteTable createTable() {
		final ByteTable table = new DefaultByteTable(DATA[0].length, DATA.length);

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

	private void checkTableModifiedColumn(final ByteTable table,
		final byte[] values, final int mod)
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

	private void checkTableModifiedRow(final ByteTable table,
		final byte[] values, final int mod)
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

	private void checkTableModifiedColumns(final ByteTable table,
		final byte[][] values, final int startMod, final int endMod)
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

	private void checkTableModifiedRows(final ByteTable table,
		final byte[][] values, final int startMod, final int endMod)
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
