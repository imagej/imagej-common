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
 * Tests {@link DefaultLongTable}.
 *
 * @author Alison Walter
 */
public class DefaultLongTableTest {

	private static final String[] HEADERS = { "Header1", "Header2" };

	private static final long[][] DATA = {
		{ -9223372036854775808l, 9223372036854775807l },
		{ 53, 0 },
		{ 92, -1098570 },
	};

	@Test
	public void testStructure() {
		final LongTable table = createTable();
		assertEquals(2, table.getColumnCount());
		assertEquals(3, table.getRowCount());
		for (final LongColumn column : table) {
			assertEquals(3, column.size());
		}

		for (int n = 0; n < table.getColumnCount(); n++) {
			assertEquals(table.get(n).getHeader(), HEADERS[n]);
		}

		for (int c = 0; c < table.getColumnCount(); c++) {
			final LongColumn columnByHeader = table.get(HEADERS[c]);
			final LongColumn columnByIndex = table.get(c);
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
		final LongTable table = createTable();
		final LongColumn col = table.get(0);
		assertEquals(col.getType(), Long.class);
	}

	@Test
	public void testAppendColumn() {
		final LongTable table = createTable();
		final long[] values = { 542908l, 9574597419085l, -11l };

		final LongColumn col = table.appendColumn("Header3");
		col.fill(values);

		// Test appending a column
		assertEquals(table.getColumnCount(), 3);
		assertEquals(table.get(2).getHeader(), "Header3");

		checkTableModifiedColumn(table, values, 2);
	}

	@Test
	public void testRemoveColumn() {
		final LongTable table = createTable();

		final LongColumn col = table.removeColumn(1);

		// Test removing a column
		for (int i = 0; i < col.size(); i++) {
			assertEquals(col.getValue(i), DATA[i][1]);
		}
		assertEquals(table.getColumnCount(), 1);

		checkTableModifiedColumn(table, null, 1);
	}

	@Test
	public void testInsertColumn() {
		final LongTable table = createTable();
		final long[] values = { 542908l, 9574597419085l, -11l };

		final LongColumn col = table.insertColumn(1, "Header3");
		col.fill(values);

		assertEquals(table.getColumnCount(), 3);
		assertEquals(table.get(1).getHeader(), "Header3");

		checkTableModifiedColumn(table, values, 1);
	}

	@Test
	public void testAppendColumns() {
		final LongTable table = createTable();
		final long[][] values =
			{
				{ 542908l, 9574597419085l, -11l },
				{ 8l, -574l, -1l },
				{ 0l, -1112313l, 98137l },
				{ 30l, 672534l, -173271l },
				{ 310249871l, -9879723194187l, -294l } };

		final String[] headers =
			{ "Header3", "Header4", "Header5", "Header6", "Header7" };
		final List<LongColumn> col = table.appendColumns(headers);
		col.get(0).fill(values[0]);
		col.get(1).fill(values[1]);
		col.get(2).fill(values[2]);
		col.get(3).fill(values[3]);
		col.get(4).fill(values[4]);

		// Test appending a column
		assertEquals(table.getColumnCount(), 7);
		assertEquals(table.get(2).getHeader(), "Header3");
		assertEquals(table.get(3).getHeader(), "Header4");
		assertEquals(table.get(4).getHeader(), "Header5");
		assertEquals(table.get(5).getHeader(), "Header6");
		assertEquals(table.get(6).getHeader(), "Header7");

		checkTableModifiedColumns(table, values, 2, 6);
	}

	@Test
	public void testRemoveColumns() {
		final LongTable table = createTable();

		final List<LongColumn> col = table.removeColumns(0, 2);

		// Test removing a column
		for (int q = 0; q < col.size(); q++) {
			for (int i = 0; i < col.get(0).size(); i++) {
				assertEquals(col.get(q).getValue(i), DATA[i][q]);
			}
		}
		assertEquals(table.getColumnCount(), 0);

		checkTableModifiedColumns(table, null, 0, 2);
	}

	@Test
	public void testInsertColumns() {
		final LongTable table = createTable();
		final long[][] values =
			{
				{ 542908l, 9574597419085l, -11l },
				{ 8l, -574l, -1l },
				{ 0l, -1112313l, 98137l },
				{ 30l, 672534l, -173271l },
				{ 310249871l, -9879723194187l, -294l } };

		final String[] headers =
			{ "Header3", "Header4", "Header5", "Header6", "Header7" };
		final List<LongColumn> col = table.insertColumns(1, headers);
		col.get(0).fill(values[0]);
		col.get(1).fill(values[1]);
		col.get(2).fill(values[2]);
		col.get(3).fill(values[3]);
		col.get(4).fill(values[4]);

		// Test appending a column
		assertEquals(table.getColumnCount(), 7);
		assertEquals(table.get(1).getHeader(), "Header3");
		assertEquals(table.get(2).getHeader(), "Header4");
		assertEquals(table.get(3).getHeader(), "Header5");
		assertEquals(table.get(4).getHeader(), "Header6");
		assertEquals(table.get(5).getHeader(), "Header7");

		checkTableModifiedColumns(table, values, 1, 5);
	}

	@Test
	public void testAppendRow() {
		final LongTable table = createTable();
		final long[] values = { 301984l, 15l };

		// Test appending a row
		table.appendRow();
		assertEquals(table.getRowCount(), 4);
		for (int i = 0; i < values.length; i++) {
			table.setValue(i, 3, values[i]);
			assertEquals(table.getValue(i, 3), values[i]);
		}

		checkTableModifiedRow(table, values, 3);
	}

	@Test
	public void testRemoveRow() {
		final LongTable table = createTable();

		// Test removing a row
		table.removeRow(0);

		assertEquals(table.getRowCount(), 2);
		for (int i = 0; i < table.getColumnCount(); i++) {
			assertEquals(table.getValue(i, 0), DATA[1][i]);
		}

		checkTableModifiedRow(table, null, 0);
	}

	@Test
	public void testInsertRow() {
		final LongTable table = createTable();
		final long[] values = { 301984l, 15l };

		table.insertRow(1);

		assertEquals(table.getRowCount(), 4);
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.setValue(i, 1, values[i]);
		}

		checkTableModifiedRow(table, values, 1);
	}

	@Test
	public void testAppendRows() {
		final LongTable table = createTable();
		final long[][] values =
			{ { 301984l, 15l }, { -12l, -10849l }, { 0l, -8l }, { 90l, -110l },
				{ -9879128347l, -91874l }, { -330l, 8910347l }, { 13214l, -98417l } };

		// Test appending a row
		table.appendRows(7);
		assertEquals(table.getRowCount(), 10);
		for (int r = 0; r < values.length; r++) {
			for (int c = 0; c < values[0].length; c++) {
				table.setValue(c, r + 3, values[r][c]);
				assertEquals(table.getValue(c, r + 3), values[r][c]);
			}
		}

		checkTableModifiedRows(table, values, 3, 10);
	}

	@Test
	public void testRemoveRows() {
		final LongTable table = createTable();
		table.removeRows(0, 2);
		assertEquals(table.getRowCount(), 1);

		checkTableModifiedRows(table, null, 0, 1);
	}

	@Test
	public void testInsertRows() {
		final LongTable table = createTable();
		final long[][] values =
			{ { 301984l, 15l }, { -12l, -10849l }, { 0l, -8l }, { 90l, -110l },
				{ -9879128347l, -91874l }, { -330l, 8910347l }, { 13214l, -98417l } };

		table.insertRows(1, 7);

		assertEquals(table.getRowCount(), 10);
		for (int r = 0; r < values.length; r++) {
			for (int c = 0; c < values[0].length; c++) {
				table.setValue(c, r + 1, values[r][c]);
				assertEquals(table.getValue(c, r + 1), values[r][c]);
			}
		}

		checkTableModifiedRows(table, values, 1, 7);
	}

	// TODO - Add more tests.

	// -- Helper methods --

	private LongTable createTable() {
		final LongTable table = new DefaultLongTable(DATA[0].length, DATA.length);

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

	private void checkTableModifiedColumn(final LongTable table,
		final long[] values, final int mod)
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

	private void checkTableModifiedRow(final LongTable table,
		final long[] values, final int mod)
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

	private void checkTableModifiedColumns(final LongTable table,
		final long[][] values, final int startMod, final int endMod)
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

	private void checkTableModifiedRows(final LongTable table,
		final long[][] values, final int startMod, final int endMod)
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
