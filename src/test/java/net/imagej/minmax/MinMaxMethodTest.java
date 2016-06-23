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

package net.imagej.minmax;

import static org.junit.Assert.assertEquals;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.Type;
import net.imglib2.util.Util;

import org.junit.Test;
import org.scijava.Context;

/**
 * Tests for the {@link MinMaxMethod} class.
 * 
 * @author Mark Hiner
 */
public class MinMaxMethodTest {

	private final Context ctx = new Context();

	/**
	 * Verifies that the DefaultMinMaxMethod will always report 100% progress.
	 * Failure to do so can cause significant problems in environments depending
	 * on progress updates to the status bar.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void verifyMinMaxStatusReport() {
		final byte[] data = new byte[50000 * 20];
		data[231014] = 100;
		final Img img = ArrayImgs.bytes(data, 50000, 20);

		TestMinMaxMethod mmMethod = new TestMinMaxMethod();
		ctx.inject(mmMethod);

		mmMethod.initialize(img);

		mmMethod.process();

		assertEquals(100, mmMethod.reported);
	}

	/**
	 * Test {@link DefaultMinMaxMethod} with a custom {@link #report(int)} method
	 * that allows us to determine how many times it was called.
	 */
	private static class TestMinMaxMethod<T extends Type<T> & Comparable<T>>
		extends DefaultMinMaxMethod<T>
	{

		// Duplicated private fields
		private IterableInterval<T> image;
		private Object lock = new Object();
		// Number of positions processed
		private long[] progress = null;
		// Limit how many times to send status updates

		// Last reported percentage
		private int reported = -1;
		private final int MAX_UPDATES = 100;
		private long imageSize;

		@Override
		public void initialize(final IterableInterval<T> interval) {
			super.initialize(interval);

			image = interval;
			imageSize = image.size();

			setNumThreads();
			progress = new long[getNumThreads()];
		}

		// Need to duplicate compute so that we call our custom report method
		@Override
		protected void compute(final int threadNumber, final long startPos,
			final long loopSize, final T min, final T max)
		{
			final Cursor<T> cursor = image.cursor();

			// init min and max
			cursor.fwd();

			min.set(cursor.get());
			max.set(cursor.get());

			cursor.reset();

			// move to the starting position of the current thread
			cursor.jumpFwd(startPos);

			// do as many pixels as wanted by this thread
			for (long j = 0; j < loopSize; ++j) {
				cursor.fwd();

				final T value = cursor.get();

				if (Util.min(min, value) == value) min.set(value);

				if (Util.max(max, value) == value) max.set(value);

				report(threadNumber);
			}
		}

		// Works the same as the DefaultMinMaxMethod#compute, but without
		// broadcasting to the StatusService.
		private void report(int threadNumber) {
			//NB: to verify this test is necessary, run this with progress[0]++
			// as ++ is not atomic, this will not reach 100% progress.
			progress[threadNumber]++;

			long netProgress = 0;
			for (int i = 0; i < progress.length; i++) {
				netProgress += progress[i];
			}
			final int percentWork =
				(int) (((double) netProgress / imageSize) * MAX_UPDATES);

			if (percentWork > reported) {
				synchronized (lock) {
					// NB: check twice in case another thread has already reported the
					// status.
					// We do this to avoid executing a synchronized block for each pixel
					// analyzed.
					if (percentWork > reported) {
						reported++;
					}
				}
			}
		}
	}
}
