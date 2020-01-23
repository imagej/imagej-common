/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2020 Board of Regents of the University of
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

package net.imagej.roi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.list.ListImg;
import net.imglib2.img.list.ListImgFactory;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.KnownConstant;
import net.imglib2.roi.Mask;
import net.imglib2.roi.MaskInterval;
import net.imglib2.roi.Masks;
import net.imglib2.roi.mask.integer.DefaultMask;
import net.imglib2.roi.mask.integer.DefaultMaskInterval;
import net.imglib2.roi.mask.integer.RandomAccessibleIntervalAsMaskInterval;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.logic.BoolType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.scijava.Context;
import org.scijava.convert.ConvertService;
import org.scijava.convert.Converter;
import org.scijava.util.GenericUtils;

/**
 * Tests {@link MaskIntervalToRAIConverter} and the correspond unwrapper.
 *
 * @author Alison Walter
 */
public class MaskIntervalToRAIConverterTest {

	private static Context context;
	private static ConvertService convertService;
	private static MaskInterval mi;
	private static RandomAccessibleIntervalAsMaskInterval<BoolType> wrap;

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void setup() {
		context = new Context(ConvertService.class);
		convertService = context.getService(ConvertService.class);

		final int seed = -12;
		final Random r = new Random(seed);
		final Img<BoolType> img = new ListImg<>(new long[] { 10, 30 },
			new BoolType());
		final Cursor<BoolType> c = img.cursor();
		while (c.hasNext())
			c.next().set(r.nextBoolean());

		wrap = (RandomAccessibleIntervalAsMaskInterval<BoolType>) Masks
			.toMaskInterval(img);
		mi = new DefaultMaskInterval(new FinalInterval(new long[] { 12, 30 }),
			BoundaryType.UNSPECIFIED, t -> t.getDoublePosition(0) % 2 == 0,
			KnownConstant.UNKNOWN);
	}

	@AfterClass
	public static void teardown() {
		context.dispose();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testConvert() {
		final Converter<MaskInterval, RandomAccessibleInterval<BoolType>> c =
			new MaskIntervalToRAIConverter();
		final RandomAccessibleInterval<?> rai = (RandomAccessibleInterval<?>) c
			.convert(mi, MaskConversionUtil.randomAccessibleIntervalType());

		assertEquals(rai.min(0), mi.min(0));
		assertEquals(rai.min(1), mi.min(1));
		assertEquals(rai.max(0), mi.max(0));
		assertEquals(rai.max(1), mi.max(1));

		assertTrue(Util.getTypeFromInterval(rai) instanceof BoolType);
		final RandomAccess<BoolType> ra = ((RandomAccessibleInterval<BoolType>) rai)
			.randomAccess();

		ra.setPosition(new long[] { 6, 5 });
		assertEquals(ra.get().get(), mi.test(ra));

		ra.fwd(0);
		assertEquals(ra.get().get(), mi.test(ra));
	}

	@Test
	public void testUnwrapper() {
		final Converter<RandomAccessibleIntervalAsMaskInterval<BoolType>, RandomAccessibleInterval<BoolType>> c =
			new Unwrappers.RandomAccessibleIntervalAsMaskIntervalUnwrapper();
		final RandomAccessibleInterval<?> rai = (RandomAccessibleInterval<?>) c
			.convert(wrap, MaskConversionUtil.randomAccessibleIntervalType());

		assertEquals(wrap.getSource().hashCode(), rai.hashCode());
	}

	@Test
	public void testConverterMatching() {
		// MaskInterval to RAI
		final Converter<?, ?> cOne = convertService.getHandler(mi,
			MaskConversionUtil.randomAccessibleIntervalType());
		assertTrue(cOne instanceof MaskIntervalToRAIConverter);

		// MaskInterval to RA
		final Converter<?, ?> cTwo = convertService.getHandler(mi,
			MaskConversionUtil.randomAccessibleType());
		assertTrue(cTwo instanceof MaskIntervalToRAIConverter);

		// Mask to RAI
		final Mask m = new DefaultMask(2, BoundaryType.UNSPECIFIED, t -> t
			.getLongPosition(1) == t.getLongPosition(0), KnownConstant.UNKNOWN);
		final Converter<?, ?> cThree = convertService.getHandler(m,
			MaskConversionUtil.randomAccessibleIntervalType());
		assertNull(cThree);

		// MaskInterval to RAI BitType
		final Converter<?, ?> cFour = convertService.getHandler(m, raiBitType());
		assertNull(cFour);
	}

	@Test
	public void testUnwrapperMatching() {
		// Wrapped MaskInterval to RAI
		final Converter<?, ?> cOne = convertService.getHandler(wrap,
			MaskConversionUtil.randomAccessibleIntervalType());
		assertTrue(
			cOne instanceof Unwrappers.RandomAccessibleIntervalAsMaskIntervalUnwrapper);

		// Wrapped MaskInterval to RA
		final Converter<?, ?> cTwo = convertService.getHandler(wrap,
			MaskConversionUtil.randomAccessibleType());
		assertTrue(
			cTwo instanceof Unwrappers.RandomAccessibleIntervalAsMaskIntervalUnwrapper);

		// Wrapped Mask to RAI
		final Mask wrapM = Masks.toMask(Views.extendZero(wrap.getSource()));
		final Converter<?, ?> cThree = convertService.getHandler(wrapM,
			MaskConversionUtil.randomAccessibleIntervalType());
		assertNull(cThree);

		// Wrapped MaskInterval BitType to RAI BoolType
		final ListImg<BitType> imgB = (new ListImgFactory<BitType>()).create(
			new long[] { 5, 5 }, new BitType());
		final MaskInterval mB = Masks.toMaskInterval(imgB);
		final Converter<?, ?> cFour = convertService.getHandler(mB,
			MaskConversionUtil.randomAccessibleIntervalType());
		assertNull(cFour);

		// Wrapped MaskInterval BitType to RAI BitType
		final Converter<?, ?> cFive = convertService.getHandler(mB, raiBitType());
		assertNull(cFive);
	}

	// -- Helper methods --

	/**
	 * Gets parameterized {@link Type} for
	 * {@code RandomAccessibleInterval<BitType>}.
	 */
	public static Type raiBitType() {
		return GenericUtils.getMethodReturnType(method(
			"randomAccessibleIntervalBitType"), MaskIntervalToRAIConverterTest.class);
	}

	private static Method method(final String name) {
		try {
			return MaskIntervalToRAIConverterTest.class.getDeclaredMethod(name);
		}
		catch (NoSuchMethodException | SecurityException exc) {
			return null;
		}
	}

	@SuppressWarnings("unused")
	private static RandomAccessibleInterval<BoolType>
		randomAccessibleIntervalBitType()
	{
		return null;
	}

}
