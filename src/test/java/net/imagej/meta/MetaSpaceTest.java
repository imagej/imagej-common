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

package net.imagej.meta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.img.list.ListImg;

import org.junit.Test;

/**
 * Unit tests for {@link MetaSpace}.
 * 
 * @author Curtis Rueden
 */
public class MetaSpaceTest {

	/** Tests {@link MetaSpace#value(Class, int...)}. */
	@Test
	public void testValue() throws Exception {
		final MetaSpace meta = new DefaultMetaSpace(3);

		assertNull(meta.value(Element.class));

		meta.set(Element.class, new Element("Au", 79));
		assertEquals("Au", meta.value(Element.class).name);
		assertEquals(79, meta.value(Element.class).number);

		meta.set(Element.class, new Element("Ag", 47));
		assertEquals("Ag", meta.value(Element.class).name);
		assertEquals(47, meta.value(Element.class).number);

		meta.set(Element.class, new Element("Cu", 29));
		assertEquals("Cu", meta.value(Element.class).name);
		assertEquals(29, meta.value(Element.class).number);

		assertNull(meta.value(Axis.class, 0));
		assertNull(meta.value(Axis.class, 1));
		assertNull(meta.value(Axis.class, 2));

		meta.set(Axis.class, new Axis("X"), 0);
		assertEquals("X", meta.value(Axis.class, 0).name);
		assertNull(meta.value(Axis.class, 1));
		assertNull(meta.value(Axis.class, 2));

		meta.set(Axis.class, new Axis("Y"), 1);
		assertEquals("X", meta.value(Axis.class, 0).name);
		assertEquals("Y", meta.value(Axis.class, 1).name);
		assertNull(meta.value(Axis.class, 2));

		meta.set(Axis.class, new Axis("Z"), 2);
		assertEquals("X", meta.value(Axis.class, 0).name);
		assertEquals("Y", meta.value(Axis.class, 1).name);
		assertEquals("Z", meta.value(Axis.class, 2).name);

		meta.set(Axis.class, new Axis("X"), 0);
		assertEquals("X", meta.value(Axis.class, 0).name);
		assertNull(meta.value(Axis.class, 1));
		assertNull(meta.value(Axis.class, 2));

		final List<Timestamp> timestampList = new ArrayList<>(6);
		timestampList.add(new Timestamp(1.2, "um"));
		timestampList.add(new Timestamp(9.8, "um"));
		timestampList.add(new Timestamp(12.3, "um"));
		timestampList.add(new Timestamp(18.7, "um"));
		timestampList.add(new Timestamp(23.4, "um"));
		timestampList.add(new Timestamp(27.6, "um"));
		final ListImg<Timestamp> timestamps = new ListImg<>(timestampList, 2, 3);
		// Attach timestamps which vary by X and Y position.
		meta.set(Timestamp.class, timestamps, new boolean[] {true, true, false});
		assertEquals(meta.values(type))
	}

	/** Tests {@link MetaSpace#values(Class)}. */
	@Test
	public void testValues() throws Exception {
		final MetaSpace meta = new DefaultMetaSpace(3);
	}

	private static class Element {
		public String name;
		public int number;
		public Element(final String name, final int number) {
			this.name = name;
			this.number = number;
		}
	}

	private static class Axis {
		public String name;
		public Axis(final String name) {
			this.name = name;
		}
	}

	private static class Timestamp {
		public double value;
		public String unit;
		public Timestamp(final double value, final String unit) {
			this.value = value;
			this.unit = unit;
		}
	}
}
