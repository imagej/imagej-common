/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2021 ImageJ developers.
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

package net.imagej;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Unit tests for {@link Position}.
 * 
 * @author Barry DeZonia
 */
public class PositionTest {

	private Extents extents;
	private Position pos;

	@Test
	public void testConstructor1() {
		extents = new Extents(new long[] { 2, 3, 4 });
		pos = new Position(extents);
		assertTrue(true);
	}

	@Test
	public void testConstructor2() {
		extents = new Extents(new long[] { 1, 2, 3 }, new long[] { 4, 5, 6 });
		pos = new Position(extents);
		assertTrue(true);
	}

	@Test
	public void testGetExtents() {
		extents = new Extents(new long[] { 2, 3, 4 });
		pos = new Position(extents);
		assertEquals(extents, pos.getExtents());
	}

	@Test
	public void testNumDimensions() {
		pos = new Extents(new long[] {}).createPosition();
		assertEquals(0, pos.numDimensions());
		pos = new Extents(new long[] { 1 }).createPosition();
		assertEquals(1, pos.numDimensions());
		pos = new Extents(new long[] { 1, 1 }).createPosition();
		assertEquals(2, pos.numDimensions());
		pos = new Extents(new long[] { 1, 1, 1 }).createPosition();
		assertEquals(3, pos.numDimensions());
		pos = new Extents(new long[] { 1, 1, 1, 1 }).createPosition();
		assertEquals(4, pos.numDimensions());
	}

	@Test
	public void testDimension() {
		pos =
			new Extents(new long[] { 2, 2, 1, 1 }, new long[] { 7, 5, 3, 1 })
				.createPosition();
		assertEquals(6, pos.dimension(0));
		assertEquals(4, pos.dimension(1));
		assertEquals(3, pos.dimension(2));
		assertEquals(1, pos.dimension(3));
	}

	@Test
	public void testHasNext() {
		pos = new Extents(new long[] {}).createPosition();
		assertFalse(pos.hasNext());
		pos = new Extents(new long[] { 1 }).createPosition();
		assertTrue(pos.hasNext());
		pos = new Extents(new long[] { 1, 5, 9, 13 }).createPosition();
		assertTrue(pos.hasNext());
		pos.last();
		assertFalse(pos.hasNext());
		pos.reset();
		assertTrue(pos.hasNext());
	}

	@Test
	public void testHasPrev() {
		pos = new Extents(new long[] {}).createPosition();
		assertFalse(pos.hasPrev());
		pos = new Extents(new long[] { 2 }).createPosition();
		assertTrue(pos.hasPrev());
		pos = new Extents(new long[] { 1, 5, 9, 13 }).createPosition();
		assertTrue(pos.hasPrev());
		pos.first();
		assertFalse(pos.hasPrev());
		pos.reset();
		assertTrue(pos.hasPrev());
	}

	@Test
	public void testReset() {
		pos =
			new Extents(new long[] { 2, 2, 2 }, new long[] { 3, 5, 7 })
				.createPosition();

		pos.reset();
		assertTrue(pos.hasNext());
		int numPos = 0;
		while (pos.hasNext()) {
			pos.fwd();
			numPos++;
		}
		assertEquals(2 * 4 * 6, numPos);

		pos.reset();
		assertTrue(pos.hasNext());
		numPos = 0;
		while (pos.hasNext()) {
			pos.fwd();
			numPos++;
		}
		assertEquals(2 * 4 * 6, numPos);

		pos.reset();
		assertTrue(pos.hasPrev());
		numPos = 0;
		while (pos.hasPrev()) {
			pos.bck();
			numPos++;
		}
		assertEquals(2 * 4 * 6, numPos);

		pos.reset();
		assertTrue(pos.hasPrev());
		numPos = 0;
		while (pos.hasPrev()) {
			pos.bck();
			numPos++;
		}
		assertEquals(2 * 4 * 6, numPos);
	}

	@Test
	public void testFirst() {
		pos =
			new Extents(new long[] { 1, 2, 3 }, new long[] { 2, 3, 4 })
				.createPosition();
		pos.first();
		assertEquals(1, pos.getLongPosition(0));
		assertEquals(2, pos.getLongPosition(1));
		assertEquals(3, pos.getLongPosition(2));
	}

	@Test
	public void testLast() {
		pos =
			new Extents(new long[] { 1, 2, 3 }, new long[] { 2, 3, 4 })
				.createPosition();
		pos.last();
		assertEquals(2, pos.getLongPosition(0));
		assertEquals(3, pos.getLongPosition(1));
		assertEquals(4, pos.getLongPosition(2));
	}

	@Test
	public void testFwd() {
		pos =
			new Extents(new long[] { 1, 1, 1, 1, 1 }, new long[] { 2, 3, 4, 5, 6 })
				.createPosition();
		for (int i = 0; i < pos.getExtents().numElements(); i++) {
			pos.fwd();
			assertEquals(i, pos.getIndex());
		}
		try {
			pos.fwd();
			fail();
		}
		catch (final IllegalStateException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testBck() {
		pos =
			new Extents(new long[] { 1, 1, 1, 1, 1 }, new long[] { 2, 3, 4, 5, 6 })
				.createPosition();
		for (long i = pos.getExtents().numElements() - 1; i >= 0; i--) {
			pos.bck();
			assertEquals(i, pos.getIndex());
		}
		try {
			pos.bck();
			fail();
		}
		catch (final IllegalStateException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testFwdInt() {
		pos =
			new Extents(new long[] { 1, 1, 1 }, new long[] { 4, 5, 6 })
				.createPosition();
		pos.first();
		assertEquals(1, pos.getLongPosition(0));
		assertEquals(1, pos.getLongPosition(1));
		assertEquals(1, pos.getLongPosition(2));
		pos.fwd(0);
		assertEquals(2, pos.getLongPosition(0));
		assertEquals(1, pos.getLongPosition(1));
		assertEquals(1, pos.getLongPosition(2));
		pos.fwd(1);
		assertEquals(2, pos.getLongPosition(0));
		assertEquals(2, pos.getLongPosition(1));
		assertEquals(1, pos.getLongPosition(2));
		pos.fwd(2);
		assertEquals(2, pos.getLongPosition(0));
		assertEquals(2, pos.getLongPosition(1));
		assertEquals(2, pos.getLongPosition(2));
	}

	@Test
	public void testBckInt() {
		pos =
			new Extents(new long[] { 1, 2, 3 }, new long[] { 6, 6, 6 })
				.createPosition();
		pos.last();
		assertEquals(6, pos.getLongPosition(0));
		assertEquals(6, pos.getLongPosition(1));
		assertEquals(6, pos.getLongPosition(2));
		pos.bck(0);
		assertEquals(5, pos.getLongPosition(0));
		assertEquals(6, pos.getLongPosition(1));
		assertEquals(6, pos.getLongPosition(2));
		pos.bck(1);
		assertEquals(5, pos.getLongPosition(0));
		assertEquals(5, pos.getLongPosition(1));
		assertEquals(6, pos.getLongPosition(2));
		pos.bck(2);
		assertEquals(5, pos.getLongPosition(0));
		assertEquals(5, pos.getLongPosition(1));
		assertEquals(5, pos.getLongPosition(2));
	}

	@Test
	public void testJumpFwd() {
		pos =
			new Extents(new long[] { 1, 1, 1 }, new long[] { 4, 5, 6 })
				.createPosition();
		pos.jumpFwd(1);
		assertEquals(1, pos.getLongPosition(0));
		assertEquals(1, pos.getLongPosition(1));
		assertEquals(1, pos.getLongPosition(2));
		pos.jumpFwd(5);
		assertEquals(2, pos.getLongPosition(0));
		assertEquals(2, pos.getLongPosition(1));
		assertEquals(1, pos.getLongPosition(2));
		try {
			pos.jumpFwd(500);
			fail();
		}
		catch (final IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testMoveLongInt() {
		pos = new Extents(new long[] { 2, 3, 4 }).createPosition();
		pos.setPosition(new long[] { 1, 2, 2 });
		pos.move((long) -1, 0);
		assertEquals(0, pos.getLongPosition(0));
		pos.move((long) 0, 1);
		assertEquals(2, pos.getLongPosition(1));
		pos.move((long) 1, 2);
		assertEquals(3, pos.getLongPosition(2));
	}

	@Test
	public void testMoveLongArray() {
		pos = new Extents(new long[] { 2, 3, 4 }).createPosition();
		pos.setPosition(new long[] { 1, 2, 2 });
		pos.move(new long[] { -1, 0, 1 });
		assertEquals(0, pos.getLongPosition(0));
		assertEquals(2, pos.getLongPosition(1));
		assertEquals(3, pos.getLongPosition(2));
	}

	@Test
	public void testMoveIntInt() {
		pos = new Extents(new long[] { 2, 3, 4 }).createPosition();
		pos.setPosition(new long[] { 1, 2, 2 });
		pos.move(-1, 0);
		assertEquals(0, pos.getLongPosition(0));
		pos.move(0, 1);
		assertEquals(2, pos.getLongPosition(1));
		pos.move(1, 2);
		assertEquals(3, pos.getLongPosition(2));
	}

	@Test
	public void testMoveIntArray() {
		pos = new Extents(new long[] { 2, 3, 4 }).createPosition();
		pos.setPosition(new long[] { 1, 2, 2 });
		pos.move(new int[] { -1, 0, 1 });
		assertEquals(0, pos.getLongPosition(0));
		assertEquals(2, pos.getLongPosition(1));
		assertEquals(3, pos.getLongPosition(2));
	}

	@Test
	public void testMoveLocalizable() {
		pos = new Extents(new long[] { 2, 3, 4 }).createPosition();
		pos.setPosition(new long[] { 1, 1, 1 });
		final Position deltas =
			new Extents(new long[] { -2, -2, -2 }, new long[] { 2, 2, 2 })
				.createPosition();
		deltas.setPosition(new long[] { -1, 0, 1 });
		pos.move(deltas);
		assertEquals(0, pos.getLongPosition(0));
		assertEquals(1, pos.getLongPosition(1));
		assertEquals(2, pos.getLongPosition(2));
	}

	@Test
	public void testSetPositionLongInt() {
		pos = new Extents(new long[] { 2, 3, 4 }).createPosition();
		pos.setPosition((long) 1, 0);
		pos.setPosition((long) 2, 1);
		pos.setPosition((long) 2, 2);
		assertEquals(1, pos.getLongPosition(0));
		assertEquals(2, pos.getLongPosition(1));
		assertEquals(2, pos.getLongPosition(2));
	}

	@Test
	public void testSetPositionLongArray() {
		pos = new Extents(new long[] { 2, 3, 4 }).createPosition();
		pos.setPosition(new long[] { 1, 2, 2 });
		assertEquals(1, pos.getLongPosition(0));
		assertEquals(2, pos.getLongPosition(1));
		assertEquals(2, pos.getLongPosition(2));
	}

	@Test
	public void testSetPositionIntInt() {
		pos = new Extents(new long[] { 2, 3, 4 }).createPosition();
		pos.setPosition(1, 0);
		pos.setPosition(2, 1);
		pos.setPosition(2, 2);
		assertEquals(1, pos.getLongPosition(0));
		assertEquals(2, pos.getLongPosition(1));
		assertEquals(2, pos.getLongPosition(2));
	}

	@Test
	public void testSetPositionIntArray() {
		pos = new Extents(new long[] { 2, 3, 4 }).createPosition();
		pos.setPosition(new int[] { 1, 2, 2 });
		assertEquals(1, pos.getLongPosition(0));
		assertEquals(2, pos.getLongPosition(1));
		assertEquals(2, pos.getLongPosition(2));
	}

	@Test
	public void testSetPositionLocalizable() {
		pos =
			new Extents(new long[] { -1, -1, -1 }, new long[] { 2, 3, 4 })
				.createPosition();
		pos.setPosition(new long[] { 1, 1, 1 });
		final Position absolute =
			new Extents(new long[] { -2, -2, -2 }, new long[] { 2, 2, 2 })
				.createPosition();
		absolute.setPosition(new long[] { -1, 0, 1 });
		pos.setPosition(absolute);
		assertEquals(-1, pos.getLongPosition(0));
		assertEquals(0, pos.getLongPosition(1));
		assertEquals(1, pos.getLongPosition(2));
	}

	@Test
	public void testSetIndex() {
		final long[] min = new long[] { 4, 3, 2, 1 };
		final long[] max = new long[] { 8, 6, 4, 2 };
		pos = new Extents(min, max).createPosition();
		long index = 0;
		for (long l = min[3]; l <= max[3]; l++) {
			for (long k = min[2]; k <= max[2]; k++) {
				for (long j = min[1]; j <= max[1]; j++) {
					for (long i = min[0]; i <= max[0]; i++) {
						pos.setIndex(index++);
						assertEquals(i, pos.getLongPosition(0));
						assertEquals(j, pos.getLongPosition(1));
						assertEquals(k, pos.getLongPosition(2));
						assertEquals(l, pos.getLongPosition(3));
					}
				}
			}
		}
		try {
			pos.setIndex(-1);
			fail();
		}
		catch (final IllegalArgumentException e) {
			assertTrue(true);
		}
		try {
			pos.setIndex(pos.getExtents().numElements());
			fail();
		}
		catch (final IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testGetIndex() {
		final long[] min = new long[] { 4, 3, 2, 1 };
		final long[] max = new long[] { 8, 6, 4, 2 };
		pos = new Extents(min, max).createPosition();
		long index = 0;
		for (long l = min[3]; l <= max[3]; l++) {
			for (long k = min[2]; k <= max[2]; k++) {
				for (long j = min[1]; j <= max[1]; j++) {
					for (long i = min[0]; i <= max[0]; i++) {
						pos.setPosition(i, 0);
						pos.setPosition(j, 1);
						pos.setPosition(k, 2);
						pos.setPosition(l, 3);
						assertEquals(index++, pos.getIndex());
					}
				}
			}
		}
	}

	@Test
	public void testLocalizeIntArray() {
		pos = new Extents(new long[] { 2, 3, 4 }).createPosition();
		pos.setPosition(new long[] { 0, 1, 3 });
		final int[] position = new int[3];
		pos.localize(position);
		assertEquals(0, position[0]);
		assertEquals(1, position[1]);
		assertEquals(3, position[2]);
	}

	@Test
	public void testLocalizeLongArray() {
		pos = new Extents(new long[] { 2, 3, 4 }).createPosition();
		pos.setPosition(new long[] { 0, 1, 3 });
		final long[] position = new long[3];
		pos.localize(position);
		assertEquals(0, position[0]);
		assertEquals(1, position[1]);
		assertEquals(3, position[2]);
	}

	@Test
	public void testLocalizeFloatArray() {
		pos = new Extents(new long[] { 2, 3, 4 }).createPosition();
		pos.setPosition(new long[] { 0, 1, 3 });
		final float[] position = new float[3];
		pos.localize(position);
		assertEquals(0f, position[0], 0);
		assertEquals(1f, position[1], 0);
		assertEquals(3f, position[2], 0);
	}

	@Test
	public void testLocalizeDoubleArray() {
		pos = new Extents(new long[] { 2, 3, 4 }).createPosition();
		pos.setPosition(new long[] { 0, 1, 3 });
		final double[] position = new double[3];
		pos.localize(position);
		assertEquals(0, position[0], 0);
		assertEquals(1, position[1], 0);
		assertEquals(3, position[2], 0);
	}

	@Test
	public void testGetIntPosition() {
		pos = new Extents(new long[] { 2, 3, 4 }).createPosition();
		pos.setPosition(new long[] { 1, 2, 2 });
		assertEquals(1, pos.getIntPosition(0));
		assertEquals(2, pos.getIntPosition(1));
		assertEquals(2, pos.getIntPosition(2));
	}

	@Test
	public void testGetLongPosition() {
		pos = new Extents(new long[] { 2, 3, 4 }).createPosition();
		pos.setPosition(new long[] { 1, 2, 2 });
		assertEquals(1, pos.getLongPosition(0));
		assertEquals(2, pos.getLongPosition(1));
		assertEquals(2, pos.getLongPosition(2));
	}

	@Test
	public void testGetFloatPosition() {
		pos = new Extents(new long[] { 2, 3, 4 }).createPosition();
		pos.setPosition(new long[] { 1, 2, 2 });
		assertEquals(1f, pos.getFloatPosition(0), 0);
		assertEquals(2f, pos.getFloatPosition(1), 0);
		assertEquals(2f, pos.getFloatPosition(2), 0);
	}

	@Test
	public void testGetDoublePosition() {
		pos = new Extents(new long[] { 2, 3, 4 }).createPosition();
		pos.setPosition(new long[] { 1, 2, 2 });
		assertEquals(1, pos.getDoublePosition(0), 0);
		assertEquals(2, pos.getDoublePosition(1), 0);
		assertEquals(2, pos.getDoublePosition(2), 0);
	}

}
