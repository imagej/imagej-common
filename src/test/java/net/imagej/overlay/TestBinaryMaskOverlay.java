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

package net.imagej.overlay;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.roi.BinaryMaskRegionOfInterest;
import net.imglib2.type.logic.BitType;

import org.junit.Test;
import org.scijava.Context;

/**
 * Unit tests for {@link BinaryMaskOverlay}.
 * 
 * @author Lee Kamentsky
 */
public class TestBinaryMaskOverlay {

	private Img<BitType> makeImg(final boolean[][] imgArray) {
		final ArrayImg<BitType, LongArray> img =
			ArrayImgs.bits(new long[] { imgArray.length, imgArray[0].length });
		img.setLinkedType(new BitType(img));
		final RandomAccess<BitType> ra = img.randomAccess();
		for (int i = 0; i < imgArray.length; i++) {
			ra.setPosition(i, 0);
			for (int j = 0; j < imgArray[i].length; j++) {
				ra.setPosition(j, 1);
				ra.get().set(imgArray[i][j]);
			}
		}
		return img;
	}

	private BinaryMaskRegionOfInterest<BitType, Img<BitType>> makeRoi(
		final boolean[][] imgArray)
	{
		return new BinaryMaskRegionOfInterest<>(
			makeImg(imgArray));
	}

	private BinaryMaskOverlay makeOverlay(final Context context, final boolean[][] imgArray) {
		return new BinaryMaskOverlay(context, makeRoi(imgArray));
	}

	@Test
	public void testWriteExternal() throws IOException {
		final Context context = new Context(true);
		final BinaryMaskOverlay overlay =
			makeOverlay(context, new boolean[][] { { true } });
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		final ObjectOutputStream out = new ObjectOutputStream(os);
		out.writeObject(overlay);
	}

	/*
	 * Disabled as of Aug 23, 2013
	 * See ticket #1991.

	@Test
	public void testReadExternal() throws IOException, ClassNotFoundException {
		final Context context = new Context(true);
		final Random r = new Random(54321);
		for (int iter = 0; iter < 100; iter++) {
			final boolean[][] imgArray = new boolean[5][5];
			for (int i = 0; i < 5; i++) {
				for (int j = 0; j < 5; j++) {
					imgArray[i][j] = r.nextBoolean();
				}
			}
			final BinaryMaskOverlay overlay = makeOverlay(context, imgArray);
			final ColorRGB colorIn =
					new ColorRGB(r.nextInt(256), r.nextInt(256), r.nextInt(256));
			overlay.setFillColor(colorIn);
			overlay.setAlpha(r.nextInt(256));
			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			final ObjectOutputStream out = new ObjectOutputStream(os);
			out.writeObject(overlay);
			final byte[] buffer = os.toByteArray();
			final ObjectInputStream in =
					new ObjectInputStream(new ByteArrayInputStream(buffer));
			final Object o = in.readObject();
			assertEquals(0, in.available());
			assertTrue(o instanceof BinaryMaskOverlay);
			final BinaryMaskOverlay overlayOut = (BinaryMaskOverlay) o;
			assertEquals(overlayOut.getAlpha(), overlay.getAlpha());
			final ColorRGB fillOut = overlayOut.getFillColor();
			assertEquals(colorIn, fillOut);
			final RealRandomAccess<BitType> ra =
					overlayOut.getRegionOfInterest().realRandomAccess();
			for (int i = 0; i < 5; i++) {
				ra.setPosition(i, 0);
				for (int j = 0; j < 5; j++) {
					ra.setPosition(j, 1);
					assertEquals(imgArray[i][j], ra.get().get());
				}
			}
		}
	}
	*/

	@Test
	public void testBinaryMaskOverlay() {
		final Context context = new Context(true);
		makeOverlay(context, new boolean[][] { { true } });
	}

}
