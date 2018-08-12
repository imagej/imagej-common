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

import org.scijava.table.DoubleColumn;

/**
 * Tests {@link DefaultResultsTable}.
 * 
 * @author Curtis Rueden
 * @author Alison Walter
 */
public class DefaultResultsTableTest {

	private static final String[] HEADERS = { "Year", "Age", "BA" };

	// Paul Molitor
	private static final double[][] DATA = {
		{1978, 21, .273},
		{1979, 22, .322},
		{1980, 23, .304},
		{1981, 24, .267},
		{1982, 25, .302},
		{1983, 26, .270},
		{1984, 27, .217},
		{1985, 28, .297},
		{1986, 29, .281},
		{1987, 30, .353},
		{1988, 31, .312},
		{1989, 32, .315},
		{1990, 33, .285},
		{1991, 34, .325},
		{1992, 35, .320},
		{1993, 36, .332},
		{1994, 37, .341},
		{1995, 38, .270},
		{1996, 39, .341},
		{1997, 40, .305},
		{1998, 41, .281},
	};

	@Test
	public void testStructure() {
		final ResultsTable table = createTable();
		assertEquals(3, table.getColumnCount());
		assertEquals(21, table.getRowCount());
		for (final DoubleColumn column : table) {
			assertEquals(21, column.size());
		}

		assertEquals("Year", table.getColumnHeader(0));
		assertEquals("Age", table.getColumnHeader(1));
		assertEquals("BA", table.getColumnHeader(2));

		for (int c = 0; c < table.getColumnCount(); c++) {
			final DoubleColumn columnByHeader = table.get(HEADERS[c]);
			final DoubleColumn columnByIndex = table.get(c);
			assertSame(columnByHeader, columnByIndex);
			assertEquals(DATA.length, columnByHeader.size());
			for (int r = 0; r < table.getRowCount(); r++) {
				assertEquals(DATA[r][c], table.getValue(c, r), 0);
				assertEquals(DATA[r][c], columnByHeader.getValue(r), 0);
			}
		}
	}

	@Test
	public void testGetColumnType() {
		final ResultsTable table = createTable();
		final DoubleColumn col = table.get(0);
		assertEquals(col.getType(), Double.class);
	}

	@Test
	public void testAppendColumn() {
		final ResultsTable table = createTable();
		final double[] values = 
			{ -0.25, 0.5, 0.625, -1.25, 0.0, 0.0325, 100.5, 13.25, 110.5, -2.25,
				4.625, -3.0, 100.0, 1209.25, -10.5, 16.25, -200.0, -0.0325,
				940385034958.5, -301284390284.25, 17.25 };

		final DoubleColumn col = table.appendColumn("Header4");
		col.fill(values);

		// Test appending a column
		assertEquals(table.getColumnCount(), 4);
		assertEquals(table.get(3).getHeader(), "Header4");

		checkTableModifiedColumn(table, values, 3);
	}

	@Test
	public void testRemoveColumn() {
		final ResultsTable table = createTable();
		final DoubleColumn col = table.removeColumn(0);

		// Test removing a column
		for (int i = 0; i < col.size(); i++) {
			assertEquals(col.getValue(i), DATA[i][0], 0);
		}
		assertEquals(table.getColumnCount(), 2);

		checkTableModifiedColumn(table, null, 0);
	}

	@Test
	public void testInsertColumn() {
		final ResultsTable table = createTable();
		final double[] values =
			{ -0.25, 0.5, 0.625, -1.25, 0.0, 0.0325, 100.5, 13.25, 110.5, -2.25,
				4.625, -3.0, 100.0, 1209.25, -10.5, 16.25, -200.0, -0.0325,
				940385034958.5, -301284390284.25, 17.25 };

		final DoubleColumn col = table.insertColumn(1, "Header4");
		col.fill(values);

		assertEquals(table.getColumnCount(), 4);
		assertEquals(table.get(1).getHeader(), "Header4");

		checkTableModifiedColumn(table, values, 1);
	}

	@Test
	public void testAppendColumns() {
		final ResultsTable table = createTable();
		final double[][] values =
			{
				{ -0.25, 0.5, 0.625, -1.25, 0.0, 0.0325, 100.5, 13.25, 110.5, -2.25,
					4.625, -3.0, 100.0, 1209.25, -10.5, 16.25, -200.0, -0.0325,
					940385034958.5, -301284390284.25, 17.25 },
				{ 0.5, 0.5, 0.25, 1.25, 0.0, 0.625, 100.5, 13.25, 11.5, -112.25, 4.625,
					-3.5, 105.0, 19.625, -10.5, 16.25, 200.0325, -0.0325, 940385034958.5,
					-1.25, 17.25 } };

		final String[] headers = { "Header4", "Header5" };
		final List<DoubleColumn> col = table.appendColumns(headers);
		col.get(0).fill(values[0]);
		col.get(1).fill(values[1]);

		// Test appending a column
		assertEquals(table.getColumnCount(), 5);
		assertEquals(table.get(3).getHeader(), "Header4");
		assertEquals(table.get(4).getHeader(), "Header5");

		checkTableModifiedColumns(table, values, 3, 4);
	}

	@Test
	public void testRemoveColumns() {
		final ResultsTable table = createTable();

		final List<DoubleColumn> col = table.removeColumns(0, 2);

		// Test removing a column
		for (int q = 0; q < col.size(); q++) {
			for (int i = 0; i < col.get(0).size(); i++) {
				assertEquals(col.get(q).getValue(i), DATA[i][q], 0);
			}
		}
		assertEquals(table.getColumnCount(), 1);

		checkTableModifiedColumns(table, null, 0, 2);
	}

	@Test
	public void testInsertColumns() {
		final ResultsTable table = createTable();
		final double[][] values =
			{
				{ -0.25, 0.5, 0.625, -1.25, 0.0, 0.0325, 100.5, 13.25, 110.5, -2.25,
					4.625, -3.0, 100.0, 1209.25, -10.5, 16.25, -200.0, -0.0325,
					940385034958.5, -301284390284.25, 17.25 },
				{ 0.5, 0.5, 0.25, 1.25, 0.0, 0.625, 100.5, 13.25, 11.5, -112.25, 4.625,
					-3.5, 105.0, 19.625, -10.5, 16.25, 200.0325, -0.0325, 940385034958.5,
					-1.25, 17.25 } };

		final String[] headers = { "Header4", "Header5" };
		final List<DoubleColumn> col = table.insertColumns(1, headers);
		col.get(0).fill(values[0]);
		col.get(1).fill(values[1]);

		assertEquals(table.getColumnCount(), 5);
		assertEquals(table.get(1).getHeader(), "Header4");
		assertEquals(table.get(2).getHeader(), "Header5");

		checkTableModifiedColumns(table, values, 1, 2);
	}

	@Test
	public void testAppendRow() {
		final ResultsTable table = createTable();
		final double[] values = { 1999, 42, 0.0 };

		// Test appending a row
		table.appendRow();
		assertEquals(table.getRowCount(), 22);
		for (int i = 0; i < values.length; i++) {
			table.setValue(i, 21, values[i]);
			assertEquals(table.getValue(i, 21), values[i], 0);
		}

		checkTableModifiedRow(table, values, 21);
	}

	@Test
	public void testRemoveRow() {
		final ResultsTable table = createTable();

		table.removeRow(4);

		assertEquals(table.getRowCount(), 20);
		for (int i = 0; i < table.getColumnCount(); i++) {
			assertEquals(table.getValue(i, 4), DATA[5][i], 0);
		}

		checkTableModifiedRow(table, null, 4);
	}

	@Test
	public void testInsertRow() {
		final ResultsTable table = createTable();
		final double[] values = { 1999, 42, 0.0 };

		table.insertRow(6);

		assertEquals(table.getRowCount(), 22);
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.setValue(i, 6, values[i]);
		}

		checkTableModifiedRow(table, values, 6);
	}

	@Test
	public void testAppendRows() {
		final ResultsTable table = createTable();
		final double[][] values = {
			{ 1999, 42, 0.123 },
			{ 2000, 43, 0.006 },
			{ 2001, 44, 0.89 },
			{ 2002, 45, 0.0 },
		};

		// Test appending a row
		table.appendRows(4);
		assertEquals(table.getRowCount(), 25);
		for (int r = 0; r < values.length; r++) {
			for (int c = 0; c < values[0].length; c++) {
				table.setValue(c, r + 21, values[r][c]);
				assertEquals(table.getValue(c, r + 21), values[r][c], 0);
			}
		}

		checkTableModifiedRows(table, values, 21, 24);
	}

	@Test
	public void testRemoveRows() {
		final ResultsTable table = createTable();
		table.removeRows(11, 6);
		assertEquals(table.getRowCount(), 15);

		checkTableModifiedRows(table, null, 11, 16);
	}

	@Test
	public void testInsertRows() {
		final ResultsTable table = createTable();
		final double[][] values = {
			{ 1999, 42, 0.123 },
			{ 2000, 43, 0.006 },
			{ 2001, 44, 0.89 },
			{ 2002, 45, 0.0 },
		};

		table.insertRows(3, 4);

		assertEquals(table.getRowCount(), 25);
		for (int r = 0; r < values.length; r++) {
			for (int c = 0; c < values[0].length; c++) {
				table.setValue(c, r + 3, values[r][c]);
				assertEquals(table.getValue(c, r + 3), values[r][c], 0);
			}
		}

		checkTableModifiedRows(table, values, 3, 6);
	}

	// TODO - Add more tests. 

	// -- Helper methods --

	private ResultsTable createTable() {
		final ResultsTable table =
			new DefaultResultsTable(DATA[0].length, DATA.length);

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

	private void checkTableModifiedColumn(final ResultsTable table,
		final double[] values, final int mod)
	{
		for (int r = 0; r < table.getRowCount(); r++) {
			for (int c = 0; c < table.getColumnCount(); c++) {
				if (c == mod && values != null) {
					assertEquals(table.getValue(c, r), values[r], 0);
				}
				else if (c > mod && values != null) {
					assertEquals(table.getValue(c, r), DATA[r][c - 1], 0);
				}
				else if (c >= mod && values == null) {
					assertEquals(table.getValue(c, r), DATA[r][c + 1], 0);
				}
				else {
					assertEquals(table.getValue(c, r), DATA[r][c], 0);
				}
			}
		}
	}

	private void checkTableModifiedRow(final ResultsTable table,
		final double[] values, final int mod)
	{
		for (int r = 0; r < table.getRowCount(); r++) {
			for (int c = 0; c < table.getColumnCount(); c++) {
				if (r == mod && values != null) {
					assertEquals(table.getValue(c, r), values[c], 0);
				}
				else if (r > mod && values != null) {
					assertEquals(table.getValue(c, r), DATA[r - 1][c], 0);
				}
				else if (r >= mod && values == null) {
					assertEquals(table.getValue(c, r), DATA[r + 1][c], 0);
				}
				else {
					assertEquals(table.getValue(c, r), DATA[r][c], 0);
				}
			}
		}
	}

	private void checkTableModifiedColumns(final ResultsTable table,
		final double[][] values, final int startMod, final int endMod)
	{
		for (int r = 0; r < table.getRowCount(); r++) {
			for (int c = 0; c < table.getColumnCount(); c++) {
				if (c >= startMod && c <= endMod && values != null) {
					assertEquals(table.getValue(c, r), values[c - startMod][r], 0);
				}
				else if (c > endMod && values != null) {
					assertEquals(table.getValue(c, r), DATA[r][c - values.length], 0);
				}
				else if (c >= startMod && values == null) {
					assertEquals(table.getValue(c, r), DATA[r][c + (endMod - startMod)],
						0);
				}
				else {
					assertEquals(table.getValue(c, r), DATA[r][c], 0);
				}
			}
		}
	}

	private void checkTableModifiedRows(final ResultsTable table,
		final double[][] values, final int startMod, final int endMod)
	{
		for (int r = 0; r < table.getRowCount(); r++) {
			for (int c = 0; c < table.getColumnCount(); c++) {
				if (r >= startMod && r <= endMod && values != null) {
					assertEquals(table.getValue(c, r), values[r - startMod][c], 0);
				}
				else if (r > endMod && values != null) {
					assertEquals(table.getValue(c, r), DATA[r - values.length][c], 0);
				}
				else if (r >= startMod && values == null) {
					assertEquals(table.getValue(c, r),
						DATA[r + (endMod - startMod + 1)][c], 0);
				}
				else {
					assertEquals(table.getValue(c, r), DATA[r][c], 0);
				}
			}
		}
	}

}
