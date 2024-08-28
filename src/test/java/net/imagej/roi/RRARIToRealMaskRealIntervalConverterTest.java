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

package net.imagej.roi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import net.imglib2.AbstractRealInterval;
import net.imglib2.Cursor;
import net.imglib2.RealInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.list.ListImg;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.roi.Masks;
import net.imglib2.roi.RealMask;
import net.imglib2.roi.RealMaskRealInterval;
import net.imglib2.roi.geom.real.ClosedWritableSphere;
import net.imglib2.roi.mask.real.RealMaskRealIntervalAsRealRandomAccessibleRealInterval;
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
 * Tests {@link RRARIToRealMaskRealIntervalConverter} and the corresponding
 * unwrapper.
 *
 * @author Alison Walter
 */
public class RRARIToRealMaskRealIntervalConverterTest {

	private static Context context;
	private static ConvertService convertService;
	private static RealRandomAccessibleRealInterval<BoolType> rrari;
	private static RealRandomAccessible<BoolType> rra;
	private static RealMaskRealIntervalAsRealRandomAccessibleRealInterval<BoolType> wrap;

	@BeforeClass
	public static void setup() {
		context = new Context(ConvertService.class);
		convertService = context.getService(ConvertService.class);

		final long seed = 0xBABEBEEFl;
		final Random rand = new Random(seed);
		final Img<BoolType> imgB = new ListImg<>(new long[] { 10, 20 },
			new BoolType());
		final Cursor<BoolType> cb = imgB.cursor();
		while (cb.hasNext())
			cb.next().set(rand.nextBoolean());
		rra = Views.interpolate(imgB, new NearestNeighborInterpolatorFactory<>());
		rrari = new TestRRARI<>(new double[] { 0, 0 }, new double[] { 10, 20 },
			rra);

		final RealMaskRealInterval rm = new ClosedWritableSphere(new double[] {
			-7.5, 4 }, 5.25);
		wrap =
			(RealMaskRealIntervalAsRealRandomAccessibleRealInterval<BoolType>) Masks
				.toRealRandomAccessibleRealInterval(rm);
	}

	@AfterClass
	public static void teardown() {
		context.dispose();
	}

	@Test
	public void testConvert() {
		final Converter<?, ?> c = new RRARIToRealMaskRealIntervalConverter();
		final RealMaskRealInterval rm = c.convert(rrari,
			RealMaskRealInterval.class);

		assertEquals(rrari.realMax(0), rm.realMax(0), 0);
		assertEquals(rrari.realMax(1), rm.realMax(1), 0);
		assertEquals(rrari.realMin(0), rm.realMin(0), 0);
		assertEquals(rrari.realMin(1), rm.realMin(1), 0);

		final RealRandomAccess<BoolType> rraB = rrari.realRandomAccess();

		rraB.setPosition(new double[] { 0.125, 5.5 });
		assertEquals(rraB.get().get(), rm.test(rraB));

		rraB.setPosition(new double[] { 3.25, 4.125 });
		assertEquals(rraB.get().get(), rm.test(rraB));
	}

	@Test
	public void testUnwrap() {
		final Converter<RealMaskRealIntervalAsRealRandomAccessibleRealInterval<BoolType>, RealMaskRealInterval> c =
			new Unwrappers.RealMaskRealIntervalAsRealRandomAccessibleRealIntervalUnwrapper();
		final RealMaskRealInterval rm = c.convert(wrap, RealMaskRealInterval.class);

		assertEquals(wrap.getSource().hashCode(), rm.hashCode());
	}

	@Test
	public void testConverterMatching() {
		// RRARI to RealMaskRealInterval (RMRI)
		final Converter<?, ?> cOne = convertService.getHandler(rrari,
			RealMaskRealInterval.class);
		assertTrue(cOne instanceof RRARIToRealMaskRealIntervalConverter);

		// RRARI to RealMask
		final Converter<?, ?> cTwo = convertService.getHandler(rrari,
			RealMask.class);
		assertTrue(cTwo instanceof RRARIToRealMaskRealIntervalConverter);

		// RRA to RMRI
		final Converter<?, ?> cThree = convertService.getHandler(rra,
			RealMaskRealInterval.class);
		assertNull(cThree);

		// RRA BitType to RMRI
		final Img<BitType> imgBi = ArrayImgs.bits(10, 20);
		final RealRandomAccessible<BitType> rraB = Views.interpolate(Views
			.extendBorder(imgBi), new NearestNeighborInterpolatorFactory<>());
		final RealRandomAccessibleRealInterval<BitType> rrariB = new TestRRARI<>(
			new double[] { -0.5, 25.5 }, new double[] { 90, 33.125 }, rraB);
		final Converter<?, ?> cFour = convertService.getHandler(rrariB,
			RealMaskRealInterval.class);
		assertNull(cFour);
	}

	@Test
	public void testUnwrapperMatching() {
		// Wrapped RRARI to RMRI
		final Converter<?, ?> cOne = convertService.getHandler(wrap,
			RealMaskRealInterval.class);
		assertTrue(
			cOne instanceof Unwrappers.RealMaskRealIntervalAsRealRandomAccessibleRealIntervalUnwrapper);

		// Wrapped RRARI to RealMask
		final Converter<?, ?> cTwo = convertService.getHandler(wrap,
			RealMask.class);
		assertTrue(
			cTwo instanceof Unwrappers.RealMaskRealIntervalAsRealRandomAccessibleRealIntervalUnwrapper);

		// Wrapped RRA to RMRI
		final RealRandomAccessible<BoolType> wrapRRA = Masks.toRealRandomAccessible(
			Masks.allRealMask(2));
		final Converter<?, ?> cThree = convertService.getHandler(wrapRRA,
			RealMaskRealInterval.class);
		assertNull(cThree);

		// Wrapped RRARI BitType to RMRI
		final RealMaskRealInterval rmri = Masks.emptyRealMaskRealInterval(2);
		final RealRandomAccessibleRealInterval<BitType> wrapB =
			new RealMaskRealIntervalAsRealRandomAccessibleRealInterval<>(rmri,
				new BitType());
		final Converter<?, ?> cFour = convertService.getHandler(wrapB,
			RealMaskRealInterval.class);
		assertNull(cFour);
	}

	// -- Helper Classes --

	private static final class TestRRARI<T> extends AbstractRealInterval
		implements RealRandomAccessibleRealInterval<T>
	{

		private final RealRandomAccessible<T> source;

		public TestRRARI(final double[] min, final double[] max,
			final RealRandomAccessible<T> source)
		{
			super(min, max);
			this.source = source;
		}

		@Override
		public RealRandomAccess<T> realRandomAccess() {
			return source.realRandomAccess();
		}

		@Override
		public RealRandomAccess<T> realRandomAccess(final RealInterval interval) {
			return source.realRandomAccess(interval);
		}

		@Override
		public T getType()
		{
			return source.getType();
		}
	}
}
