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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.scijava.Context;
import org.scijava.convert.ConvertService;

import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.roi.labeling.ImgLabeling;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

public class ImgLabelingConversionTest {

	private ConvertService convertService;

	@Before
	public void setUp() {
		final Context ctx = new Context();
		convertService = ctx.service(ConvertService.class);
	}

	@After
	public void tearDown() {
		convertService.context().dispose();
		convertService = null;
	}

	@Test
	public void testImgToImgLabelingConversion() {
		Img<UnsignedByteType> img = createTestImg();
		assertTrue(convertService.supports(img, ImgLabeling.class));
		assertEquals(RandomAccessibleIntervalToImgLabelingConverter.class,
			convertService.getHandler(img, ImgLabeling.class).getClass());

		ImgLabeling<?, ?> labeling = convertService.convert(img, ImgLabeling.class);
		assertEquals(3, labeling.getMapping().numSets());
	}

	@Test
	public void testImgLabelingToImgConversion() {
		ImgLabeling<Integer, UnsignedByteType> labeling = createTestImgLabeling();
		assertTrue(convertService.supports(labeling, Img.class));
		assertEquals(ImgLabelingToImgConverter.class, convertService.getHandler(
			labeling, Img.class).getClass());

		Img<?> img = convertService.convert(labeling, Img.class);
		assertEquals(2, ((IntegerType<?>) img.firstElement()).getInteger());
		assertEquals(3, img.dimension(0));
	}

	@Test
	public void testTwoWayConversion() {
		Img<UnsignedByteType> img = createTestImg();
		ImgLabeling<?, ?> labeling = convertService.convert(img, ImgLabeling.class);
		Img<?> converted = convertService.convert(labeling, Img.class);
		assertEquals(img.firstElement().getInteger(), ((IntegerType<?>) converted
			.firstElement()).getInteger());
	}

	@Test
	public void testIntervalViewToImgLabelingConversion() {
		Img<UnsignedByteType> img = createTestImg();
		IntervalView<UnsignedByteType> slice = Views.hyperSlice(img, 0, 1);
		assertTrue(convertService.supports(slice, ImgLabeling.class));
		assertEquals(RandomAccessibleIntervalToImgLabelingConverter.class,
			convertService.getHandler(slice, ImgLabeling.class).getClass());

		ImgLabeling<?, ?> labeling = convertService.convert(slice,
			ImgLabeling.class);
		assertEquals(3, labeling.getMapping().numSets());
	}

	private ImgLabeling<Integer, UnsignedByteType> createTestImgLabeling() {
		Img<UnsignedByteType> img = createTestImg();
		List<Integer> labels = Arrays.asList(1, 2);
		return ImgLabeling.fromImageAndLabels(img, labels);
	}

	private Img<UnsignedByteType> createTestImg() {
		byte[] array = { 2, 0, 0, 1, 1, 1, 2, 2, 0 };
		return ArrayImgs.unsignedBytes(array, 3, 3);
	}

}
