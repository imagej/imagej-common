/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2019 Board of Regents of the University of
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

import java.util.Random;

import net.imglib2.Cursor;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.list.ListImg;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.KnownConstant;
import net.imglib2.roi.Masks;
import net.imglib2.roi.RealMask;
import net.imglib2.roi.mask.real.DefaultRealMask;
import net.imglib2.roi.mask.real.RealMaskAsRealRandomAccessible;
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
 * Tests {@link RRAToRealMaskConverter} and the corresponding unwrapper.
 *
 * @author Alison Walter
 */
public class RRAToRealMaskConverterTest {

	private static Context context;
	private static ConvertService convertService;
	private static RealRandomAccessible<BoolType> rra;
	private static RealMaskAsRealRandomAccessible<BoolType> wrap;

	@BeforeClass
	public static void setup() {
		context = new Context(ConvertService.class);
		convertService = context.getService(ConvertService.class);

		final long seed = -9870214;
		final Random rand = new Random(seed);
		final Img<BoolType> imgB = new ListImg<>(new long[] { 55, 80 },
			new BoolType());
		final Cursor<BoolType> cb = imgB.cursor();
		while (cb.hasNext())
			cb.next().set(rand.nextBoolean());
		rra = Views.interpolate(Views.extendBorder(imgB),
			new NearestNeighborInterpolatorFactory<>());

		final RealMask rm = new DefaultRealMask(2, BoundaryType.UNSPECIFIED, t -> t
			.getDoublePosition(1) % 2 == 0, KnownConstant.UNKNOWN);
		wrap = (RealMaskAsRealRandomAccessible<BoolType>) Masks
			.toRealRandomAccessible(rm);
	}

	@AfterClass
	public static void teardown() {
		context.dispose();
	}

	@Test
	public void testConvert() {
		final Converter<?, ?> c = new RRAToRealMaskConverter();
		final RealMask rm = c.convert(rra, RealMask.class);

		assertEquals(rra.numDimensions(), rm.numDimensions());

		final RealRandomAccess<BoolType> rraB = rra.realRandomAccess();

		rraB.setPosition(new double[] { 11.25, -84.5 });
		assertEquals(rraB.get().get(), rm.test(rraB));

		rraB.setPosition(new double[] { 1205.25, 0.25 });
		assertEquals(rraB.get().get(), rm.test(rraB));
	}

	@Test
	public void testUnwrap() {
		final Converter<RealMaskAsRealRandomAccessible<BoolType>, RealMask> c =
			new Unwrappers.RealMaskAsRealRandomAccessibleUnwrapper();
		final RealMask rm = c.convert(wrap, RealMask.class);

		assertEquals(wrap.getSource().hashCode(), rm.hashCode());
	}

	@Test
	public void testConverterMatching() {
		// RRA to RealMask
		final Converter<?, ?> cOne = convertService.getHandler(rra, RealMask.class);
		assertTrue(cOne instanceof RRAToRealMaskConverter);

		// RRA BitType to RealMask
		final Img<BitType> imgBi = ArrayImgs.bits(5, 10);
		final RealRandomAccessible<BitType> rraB = Views.interpolate(Views
			.extendBorder(imgBi), new NearestNeighborInterpolatorFactory<>());
		final Converter<?, ?> cTwo = convertService.getHandler(rraB,
			RealMask.class);
		assertNull(cTwo);
	}

	@Test
	public void testUnwrapperMatching() {
		// Wrapped RRA to RealMask
		final Converter<?, ?> cOne = convertService.getHandler(wrap,
			RealMask.class);
		assertTrue(
			cOne instanceof Unwrappers.RealMaskAsRealRandomAccessibleUnwrapper);

		// Wrapped RRA BitType to RealMask
		final RealMask rm = Masks.allRealMask(2);
		final RealRandomAccessible<BitType> wrapB =
			new RealMaskAsRealRandomAccessible<>(rm, new BitType());
		final Converter<?, ?> cTwo = convertService.getHandler(wrapB,
			RealMask.class);
		assertNull(cTwo);
	}
}
