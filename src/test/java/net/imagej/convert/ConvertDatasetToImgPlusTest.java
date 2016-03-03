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
package net.imagej.convert;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ImgPlus;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.view.Views;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.scijava.Context;
import org.scijava.convert.ConvertService;

/**
 * Unit tests for {@link DatasetToImgPlusConverter}.
 *
 * @author Mark Hiner hinerm at gmail.com
 */
public class ConvertDatasetToImgPlusTest {

	private Context c;

	@Before
	public void setUp() {
		c = new Context();
	}

	@After
	public void tearDown() {
		c.dispose();
		c = null;
	}

	@Test
	public void testDatasetToImgPlusConverter() {
		final DatasetService datasetService = c.service(DatasetService.class);
		final ConvertService convertService = c.service(ConvertService.class);
		final Dataset ds = datasetService.create(Views.subsample(ArrayImgs.bytes(10, 10, 10), 2));
		// make sure we got the right converter back
		assertSame(DatasetToImgPlusConverter.class, convertService.getHandler(ds, ImgPlus.class).getClass());

		final ImgPlus<?> img = convertService.convert(ds, ImgPlus.class);
		// Make sure we didn't create a loop between the two
		assertFalse(img.getImg() == ds);
	}
}
