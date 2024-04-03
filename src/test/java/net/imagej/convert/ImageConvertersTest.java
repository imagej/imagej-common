/*
 * #%L
 * ImageJ2 software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2024 ImageJ2 developers.
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
import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;
import net.imagej.display.DataView;
import net.imagej.display.DatasetView;
import net.imagej.display.ImageDisplay;
import net.imagej.display.ImageDisplayService;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.test.ImgLib2Assert;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scijava.Context;
import org.scijava.convert.ConvertService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link ImageConverters}.
 *
 * @author Curtis Rueden
 */
@RunWith(Parameterized.class)
public class ImageConvertersTest {

	private Context ctx;

	@Before
	public void setUp() {
		ctx = new Context();
	}

	@After
	public void tearDown() {
		ctx.dispose();
		ctx = null;
	}

	@Parameterized.Parameter(0)
	public Class<?> srcType;

	@Parameterized.Parameter(1)
	public Class<?> destType;

	@Parameterized.Parameter(2)
	public Function<Context, RandomAccessibleInterval> imageCreator;

	@Parameterized.Parameters(name = "{0} -> {1}")
	public static Collection<Object[]> data() {
		final Map<Class<?>, Function<Context, Object>> imageTypes = new HashMap<>();
		imageTypes.put(RandomAccessibleInterval.class,
			ImageConvertersTest::createRAI);
		imageTypes.put(Img.class, ImageConvertersTest::createImg);
		imageTypes.put(ImgPlus.class, ImageConvertersTest::createImgPlus);
		imageTypes.put(Dataset.class, ImageConvertersTest::createDataset);
		imageTypes.put(DatasetView.class, ImageConvertersTest::createDatasetView);
		imageTypes.put(ImageDisplay.class, ImageConvertersTest::createImageDisplay);
		final int len = imageTypes.size();
		final Object[][] combos = new Object[len * len][3];
		final ArrayList<Object[]> params = new ArrayList<>();
		for (final Class<?> srcType : imageTypes.keySet()) {
			for (final Class<?> destType : imageTypes.keySet()) {
				params.add(new Object[] { srcType, destType, imageTypes.get(srcType) });
			}
		}
		return params;
	}

	@Test
	public void testImageConversion() {
		final ConvertService convertService = ctx.service(ConvertService.class);

		final Object src = imageCreator.apply(ctx);
		final Object dest = convertService.convert(src, destType);

		assertNotNull("Conversion failed: " + srcType.getSimpleName() + " -> " +
			destType.getSimpleName(), dest);
		assertInstance(src, srcType);
		assertInstance(dest, destType);
		ImgLib2Assert.assertImageEquals(unwrap(src), unwrap(dest));
	}

	private static RandomAccessibleInterval<FloatType> createRAI(
		final Context ctx)
	{
		// Crop an Img into an IntervalView, which is a RAI but not an Img.
		final long[] min = { 1, 2, 3 };
		final long[] max = { 2, 4, 6 };
		return Views.interval(createImg(ctx), min, max);
	}

	private static Img<FloatType> createImg(final Context ctx) {
		float[] values = new float[3 * 5 * 7];
		for (int i = 0; i < values.length; i++)
			values[i] = 1f / i;
		return ArrayImgs.floats(3, 5, 7);
	}

	private static ImgPlus<FloatType> createImgPlus(final Context ctx) {
		final String name = "decay";
		final AxisType[] axes = { Axes.X, Axes.Y, Axes.Z };
		return new ImgPlus(createImg(ctx), name, axes);
	}

	private static Dataset createDataset(final Context ctx) {
		final DatasetService datasetService = ctx.service(DatasetService.class);
		return datasetService.create(createImgPlus(ctx));
	}

	private static DatasetView createDatasetView(final Context ctx) {
		final ImageDisplayService imageDisplayService = ctx.service(
			ImageDisplayService.class);
		return imageDisplayService.createDatasetView(createDataset(ctx));
	}

	private static ImageDisplay createImageDisplay(final Context ctx) {
		final ImageDisplayService imageDisplayService = ctx.service(
			ImageDisplayService.class);
		return imageDisplayService.createImageDisplay(createDatasetView(ctx));
	}

	private static RandomAccessibleInterval<FloatType> unwrap(final Object o) {
		if (o instanceof RandomAccessibleInterval) {
			RandomAccessibleInterval<?> rai = (RandomAccessibleInterval<?>) o;
			assertInstance(Util.getTypeFromInterval(rai), FloatType.class);
			return (RandomAccessibleInterval<FloatType>) rai;
		}
		if (o instanceof DataView) return unwrap(((DataView) o).getData());
		if (o instanceof ImageDisplay) return unwrap(((ImageDisplay) o).get(0));
		throw new IllegalArgumentException("Unsupported image type: " + o.getClass()
			.getName());
	}

	private static void assertInstance(final Object o, final Class<?> c) {
		assertTrue(o.getClass().getName() + " is not a " + c.getName(), c
			.isInstance(o));
	}
}
