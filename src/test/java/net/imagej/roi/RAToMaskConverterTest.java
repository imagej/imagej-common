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

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.list.ListImg;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.KnownConstant;
import net.imglib2.roi.Mask;
import net.imglib2.roi.Masks;
import net.imglib2.roi.mask.integer.DefaultMask;
import net.imglib2.roi.mask.integer.MaskAsRandomAccessible;
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
 * Tests {@link RAToMaskConverter} and the corresponding unwrapper.
 *
 * @author Alison Walter
 */
public class RAToMaskConverterTest {

	private static Context context;
	private static ConvertService convertService;
	private static RandomAccessible<BoolType> ra;
	private static MaskAsRandomAccessible<BoolType> wrap;

	@BeforeClass
	public static void setup() {
		context = new Context(ConvertService.class);
		convertService = context.getService(ConvertService.class);

		final long seed = 87614134;
		final Random rand = new Random(seed);
		final Img<BoolType> rai = new ListImg<>(new long[] { 10, 12 },
			new BoolType());
		final Cursor<BoolType> cb = rai.cursor();
		while (cb.hasNext())
			cb.next().set(rand.nextBoolean());

		ra = Views.extendBorder(rai);

		final DefaultMask m = new DefaultMask(2, BoundaryType.UNSPECIFIED, t -> t
			.getDoublePosition(0) == t.getDoublePosition(1), KnownConstant.UNKNOWN);
		wrap = (MaskAsRandomAccessible<BoolType>) Masks.toRandomAccessible(m);
	}

	@AfterClass
	public static void teardown() {
		context.dispose();
	}

	@Test
	public void testConvert() {
		final Converter<?, ?> c = new RAToMaskConverter();
		final Mask m = c.convert(ra, Mask.class);

		assertEquals(ra.numDimensions(), m.numDimensions());

		final RandomAccess<BoolType> raB = ra.randomAccess();

		raB.setPosition(new int[] { 12, 34 });
		assertEquals(raB.get().get(), m.test(raB));

		raB.setPosition(new int[] { 0, -65 });
		assertEquals(raB.get().get(), m.test(raB));
	}

	@Test
	public void testUnwrap() {
		final Converter<MaskAsRandomAccessible<BoolType>, Mask> c =
			new Unwrappers.MaskAsRandomAccessibleUnwrapper();
		final Mask m = c.convert(wrap, Mask.class);

		assertEquals(wrap.getSource().hashCode(), m.hashCode());
	}

	@Test
	public void testConverterMatching() {
		// RA to Mask
		final Converter<?, ?> cOne = convertService.getHandler(ra, Mask.class);
		assertTrue(cOne instanceof RAToMaskConverter);

		// RA BitType to Mask
		final Img<BitType> imgBi = ArrayImgs.bits(5, 10);
		final RandomAccessible<BitType> raB = Views.extendBorder(imgBi);
		final Converter<?, ?> cTwo = convertService.getHandler(raB, Mask.class);
		assertNull(cTwo);
	}

	@Test
	public void testUnwrapperMatching() {
		// Wrapped RA to Mask
		final Converter<?, ?> cOne = convertService.getHandler(wrap, Mask.class);
		assertTrue(cOne instanceof Unwrappers.MaskAsRandomAccessibleUnwrapper);

		// Wrapped RA BitType to Mask
		final Mask m = new DefaultMask(2, BoundaryType.UNSPECIFIED, t -> t
			.getDoublePosition(0) % 2 == 0, KnownConstant.UNKNOWN);
		final RandomAccessible<BitType> wrapB = new MaskAsRandomAccessible<>(m,
			new BitType());
		final Converter<?, ?> cTwo = convertService.getHandler(wrapB, Mask.class);
		assertNull(cTwo);
	}

}
