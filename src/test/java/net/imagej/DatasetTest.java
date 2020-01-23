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

package net.imagej;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;
import net.imagej.axis.CalibratedAxis;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.IntType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.scijava.Context;

/**
 * Unit tests for {@link Dataset}.
 *
 * @author Barry DeZonia
 * @author Richard Domander
 * @author Curtis Rueden
 */
public class DatasetTest {

	// -- private interface --

	private static final int CPLANES = 2;
	private static final int ZPLANES = 3;
	private static final int TPLANES = 4;
	private static final long[] DIMENSIONS = { 4, 4, CPLANES, ZPLANES, TPLANES };

	private Context context;
	private DatasetService datasetService;

	private Dataset createDataset(final ImgFactory<IntType> factory) {
		final Img<IntType> img = factory.create(DIMENSIONS);
		final ImgPlus<IntType> imgPlus = new ImgPlus<>(img);
		return datasetService.create(imgPlus);
	}

	private Dataset createPlanarDataset() {
		return createDataset(new PlanarImgFactory<>(new IntType()));
	}

	private Dataset createNonplanarDataset() {
		return createDataset(new CellImgFactory<>(new IntType()));
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

		final RandomAccess<? extends RealType<?>> accessor = //
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
							assertEquals(planeValue(c, z, t), //
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

	@Before
	public void setUp() {
		context = new Context(DatasetService.class);
		datasetService = context.getService(DatasetService.class);
	}

	@After
	public void tearDown() {
		context.dispose();
	}

	@Test
	public void testGetPlane() {
		testPlanarCase();
		testNonplanarCase();
	}

	@Test
	public void testFactory() {
		final Dataset planar = createPlanarDataset();
		final Dataset planar2 = planar.factory().create(DIMENSIONS);
		assertDatasetsMatch(planar, planar2);
		assertSame(IntType.class, planar.getType().getClass());
		assertSame(IntType.class, planar2.getType().getClass());

		final Dataset cell = createNonplanarDataset();
		final Dataset cell2 = cell.factory().create(DIMENSIONS);
		assertDatasetsMatch(cell, cell2);
		assertSame(IntType.class, cell.getType().getClass());
		assertSame(IntType.class, cell2.getType().getClass());
	}

	private void assertDatasetsMatch(final Dataset d, final Dataset d2) {
		assertEquals(d.numDimensions(), d2.numDimensions());
		for (int i = 0; i < d.numDimensions(); i++) {
			assertEquals(d.dimension(i), d2.dimension(i));
		}
		assertSame(d.getImgPlus().getImg().getClass(), //
			d2.getImgPlus().getImg().getClass());
	}

	/**
	 * Tests dimensional convenience methods:
	 * <ul>
	 * <li>{@link Dataset#getWidth()}</li>
	 * <li>{@link Dataset#getHeight()}</li>
	 * <li>{@link Dataset#getDepth()}</li>
	 * <li>{@link Dataset#getFrames()}</li>
	 * <li>{@link Dataset#getChannels()}</li>
	 * </ul>
	 */
	@Test
	public void testDimensionConvenienceMethods() {
		final AxisType[] axes = { Axes.X, Axes.Y, Axes.Z, Axes.TIME, Axes.CHANNEL };
		final long[] dims = { 1, 2, 3, 4, 5 };
		final Dataset dataset = testData(dims, axes);

		assertEquals("Unexpected width", dims[0], dataset.getWidth());
		assertEquals("Unexpected height", dims[1], dataset.getHeight());
		assertEquals("Unexpected depth", dims[2], dataset.getDepth());
		assertEquals("Unexpected frames", dims[3], dataset.getFrames());
		assertEquals("Unexpected channels", dims[4], dataset.getChannels());
	}

	/** Tests {@link Dataset#dimension(AxisType)} for non-existent axis type. */
	@Test
	public void testDimensionReturnsOneIfDimensionDoesNotExist() {
		final AxisType[] axes = { Axes.X, Axes.Y };
		final long[] dims = { 10, 10 };
		final Dataset dataset = testData(dims, axes);

		assertEquals("Absent Z-axis should return 1", 1, dataset.dimension(Axes.Z));
	}

	/** Tests {@link Dataset#axis(AxisType)} for non-existent axis type. */
	@Test
	public void testAxisReturnsEmptyIfTypeNotFound() {
		final AxisType[] axes = { Axes.X };
		final long[] dims = { 10 };
		final Dataset dataset = testData(dims, axes);

		assertFalse("Optional should be empty, no Y-axis", //
			dataset.axis(Axes.Y).isPresent());
	}

	/** Tests {@link Dataset#axis(AxisType)}. */
	@Test
	public void testAxis() {
		final AxisType[] axes = { Axes.X, Axes.Y };
		final long[] dims = { 10, 10 };
		final Dataset dataset = testData(dims, axes);

		final Optional<CalibratedAxis> result = dataset.axis(Axes.Y);

		assertTrue("Optional should be present", result.isPresent());
		assertTrue("Wrong axis returned", //
			result.get().type().getLabel().equals(Axes.Y.getLabel()));
	}

	// -- Helper methods --

	private Dataset testData(final long[] dims, final AxisType[] axes) {
		return datasetService.create(new IntType(), dims, "Test dataset", axes);
	}
}
