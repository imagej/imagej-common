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

package net.imagej.space;

import static org.junit.Assert.assertEquals;
import net.imagej.axis.AbstractAxisTest;
import net.imagej.axis.Axes;
import net.imagej.axis.DefaultTypedAxis;
import net.imagej.axis.TypedAxis;
import net.imagej.space.DefaultTypedSpace;

import org.junit.Test;

/**
 * Tests {@link DefaultTypedSpace}.
 * 
 * @author Barry DeZonia
 */
public class DefaultTypedSpaceTest extends AbstractAxisTest {

	private DefaultTypedSpace space;

	@Test
	public void test1() {
		space = new DefaultTypedSpace(3);
		assertUnknown(space.axis(0));
		assertUnknown(space.axis(1));
		assertUnknown(space.axis(2));
		space.axis(0).setType(Axes.CHANNEL);
		space.axis(1).setType(Axes.Z);
		space.axis(2).setType(Axes.TIME);
		assertEquals(Axes.CHANNEL, space.axis(0).type());
		assertEquals(Axes.Z, space.axis(1).type());
		assertEquals(Axes.TIME, space.axis(2).type());
	}

	@Test
	public void test2() {
		final TypedAxis axis0 = new DefaultTypedAxis(Axes.CHANNEL);
		final TypedAxis axis1 = new DefaultTypedAxis(Axes.Y);
		space = new DefaultTypedSpace(axis0, axis1);
		assertEquals(Axes.CHANNEL, space.axis(0).type());
		assertEquals(Axes.Y, space.axis(1).type());
		space.axis(0).setType(Axes.CHANNEL);
		space.axis(1).setType(Axes.Z);
		assertEquals(Axes.CHANNEL, space.axis(0).type());
		assertEquals(Axes.Z, space.axis(1).type());
	}

}
