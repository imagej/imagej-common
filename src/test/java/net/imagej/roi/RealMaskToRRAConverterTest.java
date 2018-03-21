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

package net.imagej.roi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.img.Img;
import net.imglib2.img.list.ListImg;
import net.imglib2.img.list.ListImgFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.KnownConstant;
import net.imglib2.roi.Masks;
import net.imglib2.roi.RealMask;
import net.imglib2.roi.mask.real.DefaultRealMask;
import net.imglib2.roi.mask.real.RealRandomAccessibleAsRealMask;
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
 * Tests {@link RealMaskToRRAConverter} and corresponding unwrapper.
 *
 * @author Alison Walter
 */
public class RealMaskToRRAConverterTest {

	private static Context context;
	private static ConvertService convertService;
	private static RealMask rm;
	private static RealRandomAccessibleAsRealMask<BoolType> wrap;

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void setup() {
		context = new Context(ConvertService.class);
		convertService = context.getService(ConvertService.class);

		final int seed = 89;
		final Random r = new Random(seed);
		final Img<BoolType> img = new ListImg<>(new long[] { 10, 30 },
			new BoolType());
		final Cursor<BoolType> c = img.cursor();
		while (c.hasNext())
			c.next().set(r.nextBoolean());
		final RealRandomAccessible<BoolType> rra = Views.interpolate(img,
			new NearestNeighborInterpolatorFactory<BoolType>());
		wrap = (RealRandomAccessibleAsRealMask<BoolType>) Masks.toRealMask(rra);

		rm = new DefaultRealMask(3, BoundaryType.UNSPECIFIED, t -> t
			.getDoublePosition(0) + t.getDoublePosition(1) == 4,
			KnownConstant.UNKNOWN);
	}

	@AfterClass
	public static void teardown() {
		context.dispose();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testConvert() {
		final Converter<?, ?> c = new RealMaskToRRAConverter();
		final RealRandomAccessible<?> rra = (RealRandomAccessible<?>) c.convert(rm,
			MaskConversionUtil.realRandomAccessibleType());

		assertEquals(rm.numDimensions(), rra.numDimensions());
		assertTrue(MaskConversionUtil.isBoolType(rra));

		final RealRandomAccess<BoolType> rrab =
			((RealRandomAccessible<BoolType>) rra).realRandomAccess();

		rrab.setPosition(new double[] { 0, 4, -4 });
		assertEquals(rm.test(rrab), rrab.get().get());

		rrab.setPosition(new double[] { 100.5, 67, -33.125 });
		assertEquals(rm.test(rrab), rrab.get().get());
	}

	@Test
	public void testUnwrapper() {
		final Converter<RealRandomAccessibleAsRealMask<BoolType>, RealRandomAccessible<BoolType>> c =
			new Unwrappers.RealRandomAccessibleAsRealMaskUnwrapper();
		final RealRandomAccessible<?> rra = (RealRandomAccessible<?>) c.convert(
			wrap, MaskConversionUtil.realRandomAccessibleType());

		assertEquals(wrap.getSource().hashCode(), rra.hashCode());
	}

	@Test
	public void testConverterMatching() {
		// RealMask to RRA
		final Converter<?, ?> cOne = convertService.getHandler(rm,
			MaskConversionUtil.realRandomAccessibleType());
		assertTrue(cOne instanceof RealMaskToRRAConverter);

		// RealMask to RRA BitType
		final Converter<?, ?> cTwo = convertService.getHandler(rm, rraBitType());
		assertNull(cTwo);
	}

	@Test
	public void testUnwrapperMatching() {
		// Wrapped RealMask to RRA
		final Converter<?, ?> cOne = convertService.getHandler(wrap,
			MaskConversionUtil.realRandomAccessibleType());
		assertTrue(
			cOne instanceof Unwrappers.RealRandomAccessibleAsRealMaskUnwrapper);

		// Wrapped RealMask to RRA BitType
		final Converter<?, ?> cTwo = convertService.getHandler(wrap, rraBitType());
		assertNull(cTwo);

		// Wrapped RealMask BitType to RRA
		final ListImg<BitType> imgB = (new ListImgFactory<BitType>()).create(
			new long[] { 5, 5 }, new BitType());
		final RealRandomAccessible<BitType> rraB = Views.interpolate(imgB,
			new NearestNeighborInterpolatorFactory<BitType>());
		final RealMask rmB = Masks.toRealMask(rraB);
		final Converter<?, ?> cThree = convertService.getHandler(rmB,
			MaskConversionUtil.realRandomAccessibleType());
		assertNull(cThree);

		// Wrapped RealMask BitType to RRA BitType
		final Converter<?, ?> cFour = convertService.getHandler(rmB, rraBitType());
		assertNull(cFour);
	}

	// -- Helper methods --

	/**
	 * Gets parameterized {@link Type} for {@code RealRandomAccessible<BitType>}.
	 */
	private static Type rraBitType() {
		return GenericUtils.getMethodReturnType(method(
			"realRandomAccessibleBitType"), RealMaskToRRAConverterTest.class);
	}

	private static Method method(final String name) {
		try {
			return RealMaskToRRAConverterTest.class.getDeclaredMethod(name);
		}
		catch (NoSuchMethodException | SecurityException exc) {
			return null;
		}
	}

	@SuppressWarnings("unused")
	private static RandomAccessibleInterval<BitType>
		realRandomAccessibleBitType()
	{
		return null;
	}

}
