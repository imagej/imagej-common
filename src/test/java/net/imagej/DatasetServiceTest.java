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

package net.imagej;

import static org.junit.Assert.assertNotNull;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
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
}
