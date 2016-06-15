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
 * Tests {@link DefaultFloatTable}.
 *
 * @author Alison Walter
 */
public class DefaultFloatTableTest {

	private static final String[] HEADERS = { "Header1", "Header2", "Header3",
		"Header4", "Header5" };

	private static final float[][] DATA = {
		{ -4314.5f, 3214.015625f, -415.0078125f, 256f, 4.00390625f },
		{ 5.125f, 0.25f, 4325.5f, -32.0625f, -9.0078125f },
		{ 435.0009765625f, -5891.25f, -869.015625f, 3.00390625f, 75.015625f },
		{ -5.00048828125f, -78.5f, 194.125f, -93.0009765625f, 923.25f },
		{ 412f, 203985.03125f, 245.5f, -25.25f, -10943.0625f },
		{ -0.125f, 6798.00048828125f, 2435.00024414062f, -42134.25f, 0f },
		{ 35.0078125f, 298.125f, 698.0625f, 3405.5f, -3121.001953125f },
	};

	@Test
	public void testStructure() {
		final FloatTable table = createTable();
		assertEquals(5, table.getColumnCount());
		assertEquals(7, table.getRowCount());
		for (final FloatColumn column : table) {
			assertEquals(7, column.size());
		}

		for (int n = 0; n < table.getColumnCount(); n++) {
			assertEquals(table.get(n).getHeader(), HEADERS[n]);
		}

		for (int c = 0; c < table.getColumnCount(); c++) {
			final FloatColumn columnByHeader = table.get(HEADERS[c]);
			final FloatColumn columnByIndex = table.get(c);
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
		final FloatTable table = createTable();
		final FloatColumn col = table.get(0);
		assertEquals(col.getType(), Float.class);
	}

	// TODO - Add more tests.

	// -- Helper methods --

	private FloatTable createTable() {
		final FloatTable table = new DefaultFloatTable(DATA[0].length, DATA.length);

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

}
