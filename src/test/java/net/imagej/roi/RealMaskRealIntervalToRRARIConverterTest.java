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

import net.imglib2.AbstractRealInterval;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.img.Img;
import net.imglib2.img.list.ListImg;
import net.imglib2.img.list.ListImgFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.KnownConstant;
import net.imglib2.roi.Masks;
import net.imglib2.roi.RealMask;
import net.imglib2.roi.RealMaskRealInterval;
import net.imglib2.roi.geom.real.OpenWritableEllipsoid;
import net.imglib2.roi.mask.real.DefaultRealMask;
import net.imglib2.roi.mask.real.RealRandomAccessibleRealIntervalAsRealMaskRealInterval;
import net.imglib2.type.BooleanType;
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
 * Tests {@link RealMaskRealIntervalToRRARIConverter} and unwrapper.
 *
 * @author Alison Walter
 */
public class RealMaskRealIntervalToRRARIConverterTest {

	private static Context context;
	private static ConvertService convertService;
	private static RealMaskRealInterval mri;
	private static RealRandomAccessibleRealIntervalAsRealMaskRealInterval<BoolType> wrap;

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void setup() {
		context = new Context(ConvertService.class);
		convertService = context.getService(ConvertService.class);

		final int seed = 239;
		final Random r = new Random(seed);
		final Img<BoolType> img = new ListImg<>(new long[] { 10, 30 },
			new BoolType());
		final Cursor<BoolType> c = img.cursor();
		while (c.hasNext())
			c.next().set(r.nextBoolean());
		final RealRandomAccessible<BoolType> rra = Views.interpolate(img,
			new NearestNeighborInterpolatorFactory<BoolType>());
		final RealRandomAccessibleRealInterval<BoolType> rarri = new TestRRARI<>(
			new double[] { 0, 0 }, new double[] { 10, 30 }, rra);

		wrap =
			(RealRandomAccessibleRealIntervalAsRealMaskRealInterval<BoolType>) Masks
				.toRealMaskRealInterval(rarri);
		mri = new OpenWritableEllipsoid(new double[] { 12, -2.5, 6 }, new double[] {
			3, 7, 1 });
	}

	@AfterClass
	public static void teardown() {
		context.dispose();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testConvert() {
		final Converter<RealMaskRealInterval, RealRandomAccessibleRealInterval<BoolType>> c =
			new RealMaskRealIntervalToRRARIConverter();
		final RealRandomAccessibleRealInterval<?> rrari =
			(RealRandomAccessibleRealInterval<?>) c.convert(mri, MaskConversionUtil
				.realRandomAccessibleRealIntervalType());

		assertEquals(rrari.realMin(0), mri.realMin(0), 0);
		assertEquals(rrari.realMin(1), mri.realMin(1), 0);
		assertEquals(rrari.realMin(2), mri.realMin(2), 0);
		assertEquals(rrari.realMax(0), mri.realMax(0), 0);
		assertEquals(rrari.realMax(1), mri.realMax(1), 0);
		assertEquals(rrari.realMax(2), mri.realMax(2), 0);

		assertTrue(Util.getTypeFromRealInterval(rrari) instanceof BoolType);
		final RealRandomAccess<BoolType> ra =
			((RealRandomAccessibleRealInterval<BoolType>) rrari).realRandomAccess();

		ra.setPosition(new double[] { 12, -2.5, 7 });
		assertEquals(ra.get().get(), mri.test(ra));
		ra.setPosition(new double[] { 12, -2.5, 6.9 });
		assertEquals(ra.get().get(), mri.test(ra));
		ra.setPosition(new double[] { 9, 6, 22 });
		assertEquals(ra.get().get(), mri.test(ra));
		ra.setPosition(new double[] { 12, -2.5, 6 });
		assertEquals(ra.get().get(), mri.test(ra));
	}

	@Test
	public void testUnwrapper() {
		final Converter<RealRandomAccessibleRealIntervalAsRealMaskRealInterval<BoolType>, RealRandomAccessibleRealInterval<BoolType>> c =
			new Unwrappers.RealRandomAccessibleRealIntervalAsRealMaskRealIntervalUnwrapper();
		final RealRandomAccessibleRealInterval<?> rrari =
			(RealRandomAccessibleRealInterval<?>) c.convert(wrap, MaskConversionUtil
				.realRandomAccessibleRealIntervalType());

		assertEquals(wrap.getSource().hashCode(), rrari.hashCode());
	}

	@Test
	public void testConverterMatching() {
		// RealMaskRealInterval (RMRI) to RRARI
		final Converter<?, ?> cOne = convertService.getHandler(mri,
			MaskConversionUtil.realRandomAccessibleRealIntervalType());
		assertTrue(cOne instanceof RealMaskRealIntervalToRRARIConverter);

		// RMRI to RRA
		final Converter<?, ?> cTwo = convertService.getHandler(mri,
			MaskConversionUtil.realRandomAccessibleType());
		assertTrue(cTwo instanceof RealMaskRealIntervalToRRARIConverter);

		// RealMask to RRARI
		final RealMask rm = new DefaultRealMask(5, BoundaryType.CLOSED, t -> t
			.getDoublePosition(4) == 5, KnownConstant.UNKNOWN);
		final Converter<?, ?> cThree = convertService.getHandler(rm,
			MaskConversionUtil.realRandomAccessibleRealIntervalType());
		assertNull(cThree);

		// RMRI to RRARI BitType
		final Converter<?, ?> cFour = convertService.getHandler(rm, rrariBitType());
		assertNull(cFour);
	}

	@Test
	public void testUnwrapperMatching() {
		// Wrapped RMRI to RRARI
		final Converter<?, ?> cOne = convertService.getHandler(wrap,
			MaskConversionUtil.realRandomAccessibleRealIntervalType());
		assertTrue(
			cOne instanceof Unwrappers.RealRandomAccessibleRealIntervalAsRealMaskRealIntervalUnwrapper);

		// Wrapped RMRI to RRA
		final Converter<?, ?> cTwo = convertService.getHandler(wrap,
			MaskConversionUtil.realRandomAccessibleType());
		assertTrue(
			cTwo instanceof Unwrappers.RealRandomAccessibleRealIntervalAsRealMaskRealIntervalUnwrapper);

		// Wrapped RealMask to RRARI
		final Img<BitType> img = new ListImg<>(new long[] { 5, 5 }, new BitType());
		final RealRandomAccessible<BitType> rra = Views.interpolate(img,
			new NearestNeighborInterpolatorFactory<BitType>());
		final RealMask rm = Masks.toRealMask(rra);
		final Converter<?, ?> cThree = convertService.getHandler(rm,
			MaskConversionUtil.realRandomAccessibleRealIntervalType());
		assertNull(cThree);

		// Wrapped RMRI BitType to RRARI BoolType
		final ListImg<BitType> imgB = (new ListImgFactory<BitType>()).create(
			new long[] { 5, 5 }, new BitType());
		final RealRandomAccessible<BitType> rraB = Views.interpolate(imgB,
			new NearestNeighborInterpolatorFactory<BitType>());
		final RealRandomAccessibleRealInterval<BitType> rrariB = new TestRRARI<>(
			new double[] { 0, 0 }, new double[] { 5, 5 }, rraB);
		final RealMaskRealInterval wrapB = Masks.toRealMaskRealInterval(rrariB);

		final Converter<?, ?> cFour = convertService.getHandler(wrapB,
			MaskConversionUtil.realRandomAccessibleRealIntervalType());
		assertNull(cFour);

		// Wrapped RMRI BitType to RRARI BitType
		final Converter<?, ?> cFive = convertService.getHandler(wrapB,
			rrariBitType());
		assertNull(cFive);
	}

	// -- Helper methods --

	/**
	 * Gets parameterized {@link Type} for
	 * {@code RealRandomAccessibleRealInterval<BitType>}.
	 */
	private static Type rrariBitType() {
		return GenericUtils.getMethodReturnType(method(
			"realRandomAccessibleRealIntervalBitType"),
			RealMaskRealIntervalToRRARIConverterTest.class);
	}

	private static Method method(final String name) {
		try {
			return RealMaskRealIntervalToRRARIConverterTest.class.getDeclaredMethod(
				name);
		}
		catch (NoSuchMethodException | SecurityException exc) {
			return null;
		}
	}

	@SuppressWarnings("unused")
	private static RandomAccessibleInterval<BitType>
		realRandomAccessibleRealIntervalBitType()
	{
		return null;
	}

	// -- Helper classes --

	private static final class TestRRARI<B extends BooleanType<B>> extends
		AbstractRealInterval implements RealRandomAccessibleRealInterval<B>
	{

		private final RealRandomAccessible<B> rra;

		public TestRRARI(final double[] min, final double[] max,
			final RealRandomAccessible<B> rra)
		{
			super(min, max);
			this.rra = rra;
		}

		@Override
		public RealRandomAccess<B> realRandomAccess() {
			return rra.realRandomAccess();
		}

		@Override
		public RealRandomAccess<B> realRandomAccess(final RealInterval interval) {
			return rra.realRandomAccess(interval);
		}

	}
}
