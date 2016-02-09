/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2015 Board of Regents of the University of
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

package net.imagej;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;

import org.junit.AfterClass;
import org.junit.Test;
import org.scijava.Context;

/**
 * Unit tests for {@link Dataset}.
 * 
 * @author Barry DeZonia
 */
public class DatasetTest {

	// -- private interface --
	private static final Context context = new Context(DatasetService.class);
	private static final DatasetService datasetService = context.getService(DatasetService.class);

	private static final int CPLANES = 2;
	private static final int ZPLANES = 3;
	private static final int TPLANES = 4;
	private static final long[] DIMENSIONS = { 4, 4, CPLANES, ZPLANES, TPLANES };

	private Dataset createDataset(final ImgFactory<IntType> factory) {
		final Img<IntType> img = factory.create(DIMENSIONS, new IntType());
		final ImgPlus<IntType> imgPlus = new ImgPlus<IntType>(img);
		return datasetService.create(imgPlus);
	}

	private Dataset createPlanarDataset() {
		return createDataset(new PlanarImgFactory<IntType>());
	}

	private Dataset createNonplanarDataset() {
		return createDataset(new CellImgFactory<IntType>());
	}

	private int planeValue(final int c, final int z, final int t) {
		return 100 * t + 10 * z + 1 * c;
	}

	private void testPlanarCase() {
		// test planar container backed case : get by reference
		final Dataset ds = createPlanarDataset();

		final int planeSize = (int) (DIMENSIONS[0] * DIMENSIONS[1]);
		final int[][][][] planes = new int[TPLANES][][][];
		for (int t = 0; t < TPLANES; t++) {
			planes[t] = new int[ZPLANES][][];
			for (int z = 0; z < ZPLANES; z++) {
				planes[t][z] = new int[CPLANES][];
				for (int c = 0; c < CPLANES; c++) {
					planes[t][z][c] = new int[planeSize];
					for (int i = 0; i < planeSize; i++)
						planes[t][z][c][i] = planeValue(c, z, t);
				}
			}
		}

		int planeNum = 0;
		for (int t = 0; t < TPLANES; t++) {
			for (int z = 0; z < ZPLANES; z++) {
				for (int c = 0; c < CPLANES; c++) {
					ds.setPlane(planeNum++, planes[t][z][c]);
				}
			}
		}

		planeNum = 0;
		for (int t = 0; t < TPLANES; t++) {
			for (int z = 0; z < ZPLANES; z++) {
				for (int c = 0; c < CPLANES; c++) {
					final int[] plane = (int[]) ds.getPlane(planeNum++, false);
					assertSame(plane, planes[t][z][c]);
					for (int i = 0; i < planeSize; i++)
						assertEquals(planeValue(c, z, t), plane[i]);
				}
			}
		}
	}

	private void testNonplanarCase() {
		// test non planar container backed case : get by copy
		final Dataset ds = createNonplanarDataset();

		final RandomAccess<? extends RealType<?>> accessor =
			ds.getImgPlus().randomAccess();
		final long[] pos = new long[DIMENSIONS.length];
		for (int t = 0; t < TPLANES; t++) {
			pos[4] = t;
			for (int z = 0; z < ZPLANES; z++) {
				pos[3] = z;
				for (int c = 0; c < CPLANES; c++) {
					pos[2] = c;
					for (int y = 0; y < DIMENSIONS[1]; y++) {
						pos[1] = y;
						for (int x = 0; x < DIMENSIONS[0]; x++) {
							pos[0] = x;
							accessor.setPosition(pos);
							accessor.get().setReal(planeValue(c, z, t));
							assertEquals(planeValue(c, z, t),
								accessor.get().getRealDouble(), 0);
						}
					}
				}
			}
		}
		int planeNum = 0;
		for (int t = 0; t < TPLANES; t++) {
			for (int z = 0; z < ZPLANES; z++) {
				for (int c = 0; c < CPLANES; c++) {
					final int[] plane1 = (int[]) ds.getPlane(planeNum, true);
					final int[] plane2 = (int[]) ds.getPlane(planeNum, true);
					assertNotSame(plane1, plane2);
					for (int i = 0; i < DIMENSIONS[0] * DIMENSIONS[1]; i++) {
						assertEquals(planeValue(c, z, t), plane1[i]);
						assertEquals(planeValue(c, z, t), plane2[i]);
					}
					planeNum++;
				}
			}
		}
	}

	// -- public tests --

	@AfterClass
	public static void oneTimeTearDown() {
		context.dispose();
	}

	@Test
	public void testGetPlane() {
		testPlanarCase();
		testNonplanarCase();
	}

	@Test
	public void testConvenienceMethodsReturnRightDimension() {
		final AxisType[] axes = {Axes.X, Axes.Y, Axes.Z, Axes.TIME, Axes.CHANNEL};
		final long[] dimensions = {1, 2, 3, 4, 5};
		final Dataset dataset = datasetService.create(new IntType(), dimensions, "", axes);

		assertEquals("Unexpected width (x-axis size)", dimensions[0], dataset.getWidth());
		assertEquals("Unexpected height (y-axis size)", dimensions[1], dataset.getHeight());
		assertEquals("Unexpected depth (z-axis size)", dimensions[2], dataset.getDepth());
		assertEquals("Unexpected frames (time-axis size)", dimensions[3], dataset.getFrames());
		assertEquals("Unexpected channels (channel-axis size)", dimensions[4], dataset.getChannels());
	}

	@Test
	public void testGetNamedAxisReturnsMinusOneIfDimensionDoesNotExist() {
		final AxisType[] axes = {Axes.X, Axes.Y};
		final long[] dimensions = {10, 10};
		final Dataset dataset = datasetService.create(new IntType(), dimensions, "", axes);

		assertEquals("There's no Z-axis, should return -1", -1, dataset.getNamedAxisSize(Axes.Z));
	}
}
