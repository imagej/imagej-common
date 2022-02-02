/*
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

package net.imagej.roi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.list.ListImg;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.KnownConstant;
import net.imglib2.roi.Mask;
import net.imglib2.roi.MaskInterval;
import net.imglib2.roi.Masks;
import net.imglib2.roi.mask.integer.DefaultMaskInterval;
import net.imglib2.roi.mask.integer.MaskIntervalAsRandomAccessibleInterval;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.logic.BoolType;
import net.imglib2.view.Views;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.scijava.Context;
import org.scijava.convert.ConvertService;
import org.scijava.convert.Converter;

/**
 * Tests {@link RAIToMaskIntervalConverter} and the corresponding unwrapper.
 *
 * @author Alison Walter
 */
public class RAIToMaskIntervalConverterTest {

	private static Context context;
	private static ConvertService convertService;
	private static RandomAccessibleInterval<BoolType> rai;
	private static MaskIntervalAsRandomAccessibleInterval<BoolType> wrap;

	@BeforeClass
	public static void setup() {
		context = new Context(ConvertService.class);
		convertService = context.getService(ConvertService.class);

		final long seed = 87614134;
		final Random rand = new Random(seed);
		rai = new ListImg<>(new long[] { 10, 12 }, new BoolType());
		final Cursor<BoolType> cb = ((Img<BoolType>) rai).cursor();
		while (cb.hasNext())
			cb.next().set(rand.nextBoolean());

		final MaskInterval mi = new DefaultMaskInterval(new FinalInterval(
			new long[] { -6, 8 }, new long[] { 22, 30 }), BoundaryType.UNSPECIFIED,
			t -> t.getLongPosition(0) % 2 != 0, KnownConstant.UNKNOWN);
		wrap = (MaskIntervalAsRandomAccessibleInterval<BoolType>) Masks
			.toRandomAccessibleInterval(mi);
	}

	@AfterClass
	public static void teardown() {
		context.dispose();
	}

	@Test
	public void testConvert() {
		final Converter<?, ?> c = new RAIToMaskIntervalConverter();
		final MaskInterval mi = c.convert(rai, MaskInterval.class);

		assertEquals(rai.realMax(0), mi.realMax(0), 0);
		assertEquals(rai.realMax(1), mi.realMax(1), 0);
		assertEquals(rai.realMin(0), mi.realMin(0), 0);
		assertEquals(rai.realMin(1), mi.realMin(1), 0);

		final RandomAccess<BoolType> raB = rai.randomAccess();

		raB.setPosition(new long[] { 8, 1 });
		assertEquals(raB.get().get(), mi.test(raB));

		raB.setPosition(new long[] { 2, 11 });
		assertEquals(raB.get().get(), mi.test(raB));
	}

	@Test
	public void testUnwrap() {
		final Converter<MaskIntervalAsRandomAccessibleInterval<BoolType>, MaskInterval> c =
			new Unwrappers.MaskIntervalAsRandomAccessibleIntervalUnwrapper();
		final MaskInterval rm = c.convert(wrap, MaskInterval.class);

		assertEquals(wrap.getSource().hashCode(), rm.hashCode());
	}

	@Test
	public void testConverterMatching() {
		// RAI to MaskInterval
		final Converter<?, ?> cOne = convertService.getHandler(rai,
			MaskInterval.class);
		assertTrue(cOne instanceof RAIToMaskIntervalConverter);

		// RAI to Mask
		final Converter<?, ?> cTwo = convertService.getHandler(rai, Mask.class);
		assertTrue(cTwo instanceof RAIToMaskIntervalConverter);

		// RA to MaskInterval
		final Converter<?, ?> cThree = convertService.getHandler(Views.extendBorder(
			rai), MaskInterval.class);
		assertNull(cThree);

		// RAI BitType to MaskInterval
		final Img<BitType> imgBi = ArrayImgs.bits(5, 10);
		final Converter<?, ?> cFour = convertService.getHandler(imgBi,
			MaskInterval.class);
		assertNull(cFour);
	}

	@Test
	public void testUnwrapperMatching() {
		// Wrapped RAI to MaskInterval
		final Converter<?, ?> cOne = convertService.getHandler(wrap,
			MaskInterval.class);
		assertTrue(
			cOne instanceof Unwrappers.MaskIntervalAsRandomAccessibleIntervalUnwrapper);

		// Wrapped RAI to Mask
		final Converter<?, ?> cTwo = convertService.getHandler(wrap, Mask.class);
		assertTrue(
			cTwo instanceof Unwrappers.MaskIntervalAsRandomAccessibleIntervalUnwrapper);

		// Wrapped RA to MaskInterval
		final RandomAccessible<BoolType> wrapRA = Masks.toRandomAccessible(Masks
			.allMask(2));
		final Converter<?, ?> cThree = convertService.getHandler(wrapRA,
			MaskInterval.class);
		assertNull(cThree);

		// Wrapped RAI BitType to MaskInterval
		final MaskInterval mi = new DefaultMaskInterval(new FinalInterval(
			new long[] { 1, 5 }, new long[] { 6, 10 }), BoundaryType.UNSPECIFIED,
			t -> t.getDoublePosition(0) == 0, KnownConstant.UNKNOWN);
		final RandomAccessibleInterval<BitType> wrapB =
			new MaskIntervalAsRandomAccessibleInterval<>(mi, new BitType());
		final Converter<?, ?> cFour = convertService.getHandler(wrapB,
			MaskInterval.class);
		assertNull(cFour);
	}
}
