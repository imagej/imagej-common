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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import net.imagej.axis.Axes;
import net.imagej.axis.DefaultLinearAxis;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.test.ImgLib2Assert;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;

import org.junit.Test;
import org.scijava.Context;

/**
 * Unit tests for {@link DefaultDatasetService}.
 * 
 * @author Curtis Rueden
 */
public class DatasetServiceTest {

	@Test
	public void testCreateFromRAI() {
		final Context ctx = new Context(DatasetService.class);
		final DatasetService datasetService = ctx.service(DatasetService.class);

		final Img<UnsignedByteType> img = ArrayImgs.unsignedBytes(5, 7);

		// NB: We use Views to create a RAI that is not an Img.
		final RandomAccessibleInterval<UnsignedByteType> rai = Views.interval(img, img);

		// Wrap the RAI in a Dataset.
		final Dataset dataset = datasetService.create(rai);

		// Ensure we can successfully make a copy. This is a
		// regression test for 6156a14da88daf110b1366282a7c3ffc02190270.
		final Img<?> copy = dataset.copy();
		assertNotNull(copy);

		ctx.dispose();
	}

	@Test
	public void testCreateFromColoredImgPlus() {
		// setup
		final Context context = new Context(DatasetService.class);
		final DatasetService datasetService =
				context.service(DatasetService.class);
		final DefaultLinearAxis xAxis = new DefaultLinearAxis(Axes.X, "um", 2);
		final DefaultLinearAxis yAxis = new DefaultLinearAxis(Axes.Y, "um", 1);
		Img<ARGBType> argbs =
				ArrayImgs.argbs(new int[] {0x010203, 0x050607}, 2, 1);
		ImgPlus<ARGBType> image =
				new ImgPlus<>(argbs, "TestName", xAxis, yAxis);

		// process
		final Dataset dataset = datasetService.create( image);

		// Test if the resulting dataset is correct.
		Img<UnsignedByteType> expected = ArrayImgs.unsignedBytes(
				new byte[] {1, 5, 2, 6, 3, 7}, 2, 1, 3);
		ImgLib2Assert.assertImageEqualsRealType(expected, dataset, 0.0);
		assertTrue(dataset.isRGBMerged());
		assertSame(xAxis, dataset.axis(0));
		assertSame(yAxis, dataset.axis(1));
		assertEquals(Axes.CHANNEL, dataset.axis(2).type());
	}
}
