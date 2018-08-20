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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

/**
 * Tests {@link DefaultGenericTable}.
 *
 * @author Alison Walter
 * 
 * @deprecated replaced by {@link org.scijava.table.DefaultGenericTableTest}
 */
@Deprecated
public class DefaultGenericTableTest {

	@Test
	public void testTypes() {
		final GenericTable table = makeTable();
		table.appendColumn();
		assertEquals(table.get(0).getType(), Byte.class);
		assertEquals(table.get(1).getType(), Character.class);
		assertEquals(table.get(2).getType(), Float.class);
		assertEquals(table.get(3).getType(), String.class);
		assertEquals(table.get(4).getType(), Object.class);
		assertTrue(GenericColumn.class.isInstance(table.get(4)));
	}

	@Test
	public void testValues() {
		final GenericTable table = makeTable();

		assertTrue(Byte.class.isInstance(table.get(0, 0)));
		assertEquals(table.get(0, 3), (byte) 0);
		assertEquals(table.get(0).getHeader(), "ByteHeader");

		assertTrue(Character.class.isInstance(table.get(1, 0)));
		assertEquals(table.get(1, 0), 'A');
		assertEquals(table.get(1).getHeader(), "CharHeader");

		assertTrue(Float.class.isInstance(table.get(2, 0)));
		assertEquals(table.get(2, 2), -100.25f);
		assertEquals(table.get(2).getHeader(), "FloatHeader");

		assertTrue(String.class.isInstance(table.get(3, 0)));
		assertEquals(table.get(3, 1), "hello");
		assertEquals(table.get(3).getHeader(), "StringHeader");
	}

	@Test
	public void testAddOps() {
		final GenericTable table = makeTable();
		final BoolColumn bcol = new BoolColumn();
		final boolean[] bval = { true, true, true, false };
		bcol.fill(bval);

		table.add(1, bcol);
		assertTrue(BoolColumn.class.isInstance(table.get(1)));

		final Collection<Column<? extends Object>> c = new ArrayList<>();
		final ShortColumn scol = new ShortColumn();
		final short[] sval = { 1, 11234, -13124, -18 };
		scol.fill(sval);
		c.add(scol);
		c.add(bcol);

		table.addAll(c);
		assertTrue(ShortColumn.class.isInstance(table.get(5)));
		assertTrue(BoolColumn.class.isInstance(table.get(6)));
		assertEquals(table.getRowCount(), 4);
		assertEquals(table.getColumnCount(), 7);

		final IntColumn icol = new IntColumn();
		final int[] ival = { 1, 2, 3, 4, 5, 6, 7, 8 };
		icol.fill(ival);
		c.add(icol);

		table.addAll(0, c);
		assertTrue(ShortColumn.class.isInstance(table.get(0)));
		assertTrue(BoolColumn.class.isInstance(table.get(1)));
		assertTrue(IntColumn.class.isInstance(table.get(2)));
		assertEquals(table.getRowCount(), 8);
		assertEquals(table.getColumnCount(), 10);
		assertEquals(table.get(3, 6), (byte) 0);
	}

	// TODO add more tests

	// -- Helper methods --

	private GenericTable makeTable() {
		// create table and columns
		final GenericTable table = new DefaultGenericTable();
		final ByteColumn col = new ByteColumn("ByteHeader");
		final CharColumn col1 = new CharColumn("CharHeader");
		final FloatColumn col2 = new FloatColumn("FloatHeader");
		final DefaultColumn<String> col3 =
			new DefaultColumn<>(String.class, "StringHeader");

		// fill columns with values
		final byte[] bval = { 3, 127, -128, 0 };
		final char[] cval = { 'A', '&', '\t', '4' };
		final float[] fval = { 12.125f, 100.5f, -100.25f, -0.03125f };
		final String[] sval = { "hi", "hello", "good day", "greetings!" };
		col.fill(bval);
		col1.fill(cval);
		col2.fill(fval);
		col3.setArray(sval.clone());
		col3.setSize(sval.length);

		// add columns to table
		table.add(col);
		table.add(col1);
		table.add(col2);
		table.add(col3);
		return table;
	}

}
