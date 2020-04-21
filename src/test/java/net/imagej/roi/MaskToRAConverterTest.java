/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2020 ImageJ developers.
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
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.list.ListImg;
import net.imglib2.img.list.ListImgFactory;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.KnownConstant;
import net.imglib2.roi.Mask;
import net.imglib2.roi.Masks;
import net.imglib2.roi.mask.integer.DefaultMask;
import net.imglib2.roi.mask.integer.RandomAccessibleAsMask;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.logic.BoolType;
import net.imglib2.view.Views;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.scijava.Context;
import org.scijava.convert.ConvertService;
import org.scijava.convert.Converter;
import org.scijava.util.GenericUtils;

/**
 * Tests {@link MaskToRAConverter} and corresponding unwrapper.
 *
 * @author Alison Walter
 */
public class MaskToRAConverterTest {

	private static Context context;
	private static ConvertService convertService;
	private static Mask m;
	private static RandomAccessibleAsMask<BoolType> wrap;

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void setup() {
		context = new Context(ConvertService.class);
		convertService = context.getService(ConvertService.class);

		m = new DefaultMask(4, BoundaryType.UNSPECIFIED, t -> t.getLongPosition(
			0) == t.getLongPosition(3), KnownConstant.UNKNOWN);

		final int seed = 82;
		final Random r = new Random(seed);
		final Img<BoolType> img = new ListImg<>(new long[] { 10, 30 },
			new BoolType());
		final Cursor<BoolType> c = img.cursor();
		while (c.hasNext())
			c.next().set(r.nextBoolean());
		final RandomAccessible<BoolType> ra = Views.extendZero(img);
		wrap = (RandomAccessibleAsMask<BoolType>) Masks.toMask(ra);
	}

	@AfterClass
	public static void teardown() {
		context.dispose();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testConvert() {
		final Converter<?, ?> c = new MaskToRAConverter();
		final RandomAccessible<?> ra = (RandomAccessible<?>) c.convert(m,
			MaskConversionUtil.randomAccessibleType());

		assertEquals(m.numDimensions(), ra.numDimensions());
		assertTrue(MaskConversionUtil.isBoolType(ra));
		final RandomAccess<BoolType> rab = ((RandomAccessible<BoolType>) ra)
			.randomAccess();

		rab.setPosition(new int[] { 7, -5, 7, 12 });
		assertEquals(m.test(rab), rab.get().get());

		rab.setPosition(new int[] { 12, 100, -82, 11 });
		assertEquals(m.test(rab), rab.get().get());
	}

	@Test
	public void testUnwrapper() {
		final Converter<RandomAccessibleAsMask<BoolType>, RandomAccessible<BoolType>> c =
			new Unwrappers.RandomAccessibleAsMaskUnwrapper();
		final RandomAccessible<?> ra = (RandomAccessible<?>) c.convert(wrap,
			MaskConversionUtil.randomAccessibleType());

		assertEquals(wrap.getSource().hashCode(), ra.hashCode());
	}

	@Test
	public void testConverterMatching() {
		// Mask to RA
		final Converter<?, ?> cOne = convertService.getHandler(m, MaskConversionUtil
			.randomAccessibleType());
		assertTrue(cOne instanceof MaskToRAConverter);

		// Mask to RA BitType
		final Converter<?, ?> cTwo = convertService.getHandler(m, raBitType());
		assertNull(cTwo);
	}

	@Test
	public void testUnwrapperMatching() {
		// Wrapped Mask to RA
		final Converter<?, ?> cOne = convertService.getHandler(wrap,
			MaskConversionUtil.randomAccessibleType());
		assertTrue(cOne instanceof Unwrappers.RandomAccessibleAsMaskUnwrapper);

		// Wrapped Mask BoolType to RA BitType
		final Converter<?, ?> cTwo = convertService.getHandler(wrap, raBitType());
		assertNull(cTwo);

		// Wrapped Mask BitType to RA BoolType
		final ListImg<BitType> imgB = (new ListImgFactory<BitType>()).create(
			new long[] { 5, 5 }, new BitType());
		final RandomAccessible<BitType> raB = Views.extendBorder(imgB);
		final Mask wrapB = Masks.toMask(raB);
		final Converter<?, ?> cThree = convertService.getHandler(wrapB,
			MaskConversionUtil.randomAccessibleType());
		assertNull(cThree);

		// Wrapped Mask BitType to RA BitType
		final Converter<?, ?> cFour = convertService.getHandler(wrapB, raBitType());
		assertNull(cFour);
	}

	// -- Helper methods --

	/**
	 * Gets parameterized {@link Type} for {@code RandomAccessible<BitType>}.
	 */
	private static Type raBitType() {
		return GenericUtils.getMethodReturnType(method("randomAccessibleBitType"),
			MaskToRAConverterTest.class);
	}

	private static Method method(final String name) {
		try {
			return MaskToRAConverterTest.class.getDeclaredMethod(name);
		}
		catch (NoSuchMethodException | SecurityException exc) {
			return null;
		}
	}

	@SuppressWarnings("unused")
	private static RandomAccessibleInterval<BitType> randomAccessibleBitType() {
		return null;
	}

}
