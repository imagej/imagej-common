/*-
 * #%L
 * ImageJ2 software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2022 ImageJ2 developers.
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

import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ImgPlus;
import net.imagej.display.ColorTables;
import net.imagej.display.DatasetView;
import net.imagej.display.ImageDisplay;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import net.imglib2.view.Views;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.scijava.Context;
import org.scijava.convert.ConvertService;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * Additional (non-parameterized) tests for {@link ImageConverters}.
 *
 * @author Gabriel Selzer
 * @author Mark Hiner hinerm at gmail.com
 * @see ImageConvertersTest
 */
public class ImageConvertersExtraTest {

	private Context ctx;
	private ConvertService convertService;
	private DatasetService datasetService;

	@Before
	public void setUp() {
		ctx = new Context();
		convertService = ctx.service(ConvertService.class);
		datasetService = ctx.service(DatasetService.class);
	}

	@After
	public void tearDown() {
		ctx.dispose();
		convertService = null;
		datasetService = null;
	}

	@Test
	public void testDatasetToDatasetView() {
		RandomAccessibleInterval<UnsignedByteType> rai = ArrayImgs.unsignedBytes(10,
			10, 10);
		Dataset d = datasetService.create(rai);
		DatasetView view = convertService.convert(d, DatasetView.class);
		Assert.assertEquals(d, view.getData());
	}

	/**
	 * Ensures reasonable color tables after the conversion
	 */
	@Test
	public void testDefaultColorTables() {
		RandomAccessibleInterval<UnsignedByteType> rai = ArrayImgs.unsignedBytes(10,
			10, 10);
		Dataset d = datasetService.create(rai);
		DatasetView view = convertService.convert(d, DatasetView.class);
		Assert.assertEquals(view.getChannelCount(), view.getColorTables().size());
		Assert.assertEquals(ColorTables.GRAYS, view.getColorTables().get(0));
	}

	@Test
	public void testDatasetToImgPlus() {
		final Dataset ds = datasetService.create(Views.subsample(ArrayImgs.bytes(10,
			10, 10), 2));
		// make sure we got the right converter back
		assertSame(ImageConverters.DatasetToImgPlusConverter.class, convertService
			.getHandler(ds, ImgPlus.class).getClass());

		final ImgPlus<?> img = convertService.convert(ds, ImgPlus.class);
		// Make sure we didn't create a loop between the two
		assertFalse(img.getImg() == ds);
	}

	@Test
	public void testRAIToImageDisplay() {
		RandomAccessibleInterval<UnsignedByteType> rai = ArrayImgs.unsignedBytes(10,
			10, 10);
		ImageDisplay display = convertService.convert(rai, ImageDisplay.class);
		assertNotNull(display);
	}

	@Test
	public void testDatasetToImageDisplay() {
		RandomAccessibleInterval<UnsignedByteType> rai = ArrayImgs.unsignedBytes(10,
			10, 10);
		Dataset d = datasetService.create(rai);
		ImageDisplay display = convertService.convert(d, ImageDisplay.class);
		Assert.assertTrue(display.isDisplaying(d));
	}
}
