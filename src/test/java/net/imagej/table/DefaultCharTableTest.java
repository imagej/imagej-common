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
 * Tests {@link DefaultCharTable}.
 *
 * @author Alison Walter
 */
public class DefaultCharTableTest {

	private static final String[] HEADERS = { "Header1", "Header2", "Header3" };

	private static final char[][] DATA = {
		{ 'a', 'B', 'c' },
		{ 'D', 'e', 'f' },
		{ 'g', 'h', 'I'},
		{ 'J', 'K', 'l' },
		{ 'm', 'N', 'O' },
		{ 'P', 'q', 'R' },
		{ 's', 't', 'u' },
		{ 'V', 'W', 'X' },
		{ 'y', '&', 'z'},
	};

	@Test
	public void testStructure() {
		final CharTable table = createTable();
		assertEquals(3, table.getColumnCount());
		assertEquals(9, table.getRowCount());
		for (final CharColumn column : table) {
			assertEquals(9, column.size());
		}

		for (int n = 0; n < table.getColumnCount(); n++) {
			assertEquals(table.get(n).getHeader(), HEADERS[n]);
		}

		for (int c = 0; c < table.getColumnCount(); c++) {
			final CharColumn columnByHeader = table.get(HEADERS[c]);
			final CharColumn columnByIndex = table.get(c);
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
		final CharTable table = createTable();
		final CharColumn col = table.get(0);
		assertEquals(col.getType(), Character.class);
	}

	@Test
	public void testAppendColumn() {
		final CharTable table = createTable();
		final char[] values = { '2', 'W', '!', '*', 'o', 'E', ' ', 'A', '\t' };

		final CharColumn col = table.appendColumn("Header4");
		col.fill(values);

		// Test appending a column
		assertEquals(table.getColumnCount(), 4);
		assertEquals(table.get(3).getHeader(), "Header4");

		checkTableModifiedColumn(table, values, 3);
	}

	@Test
	public void testRemoveColumn() {
		final CharTable table = createTable();

		final CharColumn col = table.removeColumn(2);

		// Test removing a column
		for (int i = 0; i < col.size(); i++) {
			assertEquals(col.getValue(i), DATA[i][2]);
		}
		assertEquals(table.getColumnCount(), 2);

		checkTableModifiedColumn(table, null, 2);
	}

	@Test
	public void testInsertColumn() {
		final CharTable table = createTable();
		final char[] values = { '2', 'W', '!', '*', 'o', 'E', ' ', 'A', '\t' };

		final CharColumn col = table.insertColumn(2, "Header4");
		col.fill(values);

		assertEquals(table.getColumnCount(), 4);
		assertEquals(table.get(2).getHeader(), "Header4");

		checkTableModifiedColumn(table, values, 2);
	}

	@Test
	public void testAppendRow() {
		final CharTable table = createTable();
		final char[] values = { '\t', '\uffff', '\u0000' };

		// Test appending a row
		table.appendRow();
		assertEquals(table.getRowCount(), 10);
		for (int i = 0; i < values.length; i++) {
			table.setValue(i, 9, values[i]);
			assertEquals(table.getValue(i, 9), values[i]);
		}

		checkTableModifiedRow(table, values, 9);
	}

	@Test
	public void testRemoveRow() {
		final CharTable table = createTable();

		// Test removing a row
		table.removeRow(7);
		assertEquals(table.getRowCount(), 8);
		for (int i = 0; i < table.getColumnCount(); i++) {
			assertEquals(table.getValue(i, 7), DATA[8][i]);
		}

		checkTableModifiedRow(table, null, 7);
	}

	@Test
	public void testInsertRow() {
		final CharTable table = createTable();
		final char[] values = { '\t', '\uffff', '\u0000' };

		table.insertRow(7);

		assertEquals(table.getRowCount(), 10);
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.setValue(i, 7, values[i]);
		}

		checkTableModifiedRow(table, values, 7);
	}

	// TODO - Add more tests.

	// -- Helper methods --

	private CharTable createTable() {
		final CharTable table = new DefaultCharTable(DATA[0].length, DATA.length);

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

	private void checkTableModifiedColumn(final CharTable table,
		final char[] values, final int mod)
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

	private void checkTableModifiedRow(final CharTable table, final char[] values,
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

}
