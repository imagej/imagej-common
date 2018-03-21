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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.imagej.ImgPlus;
import net.imglib2.KDTree;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealPointSampleList;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.img.Img;
import net.imglib2.img.list.ListImg;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.MaskPredicate;
import net.imglib2.roi.RealMask;
import net.imglib2.roi.geom.real.Box;
import net.imglib2.roi.geom.real.ClosedWritableSphere;
import net.imglib2.roi.geom.real.DefaultWritablePointMask;
import net.imglib2.roi.geom.real.OpenWritableBox;
import net.imglib2.roi.geom.real.PointMask;
import net.imglib2.roi.geom.real.RealPointCollection;
import net.imglib2.roi.geom.real.Sphere;
import net.imglib2.roi.geom.real.WritableBox;
import net.imglib2.roi.geom.real.WritableEllipsoid;
import net.imglib2.roi.geom.real.WritableLine;
import net.imglib2.roi.geom.real.WritablePointMask;
import net.imglib2.roi.geom.real.WritablePolygon2D;
import net.imglib2.roi.geom.real.WritablePolyline;
import net.imglib2.roi.geom.real.WritableRealPointCollection;
import net.imglib2.roi.geom.real.WritableSphere;
import net.imglib2.roi.geom.real.WritableSuperEllipsoid;
import net.imglib2.roi.mask.integer.RandomAccessibleIntervalAsMaskInterval;
import net.imglib2.roi.mask.real.RealMaskRealIntervalAsRealRandomAccessibleRealInterval;
import net.imglib2.type.logic.BoolType;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.scijava.Context;
import org.scijava.Priority;
import org.scijava.convert.AbstractConverter;
import org.scijava.convert.ConvertService;
import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;

import gnu.trove.list.array.TDoubleArrayList;

/**
 * Tests {@link DefaultROIService}.
 *
 * @author Alison Walter
 */
public class DefaultROIServiceTest {

	private static Context context;
	private static ROIService roi;
	private static List<RealLocalizable> points;

	@BeforeClass
	public static void setupOnce() {
		context = new Context(ROIService.class, ConvertService.class);
		roi = context.getService(ROIService.class);
		points = new ArrayList<>(3);
		points.add(new RealPoint(new double[] { 2.5, 6 }));
		points.add(new RealPoint(new double[] { 3, 10 }));
		points.add(new RealPoint(new double[] { 22, 4 }));
	}

	@AfterClass
	public static void teardownOnce() {
		context.dispose();
	}

	// -- Test Conversions --

	@Test
	public void testToMaskPredicate() {
		final Img<BoolType> img = new ListImg<>(new long[] { 12, 52 },
			new BoolType());
		final ImgPlus<BoolType> plus = new ImgPlus<>(img);
		final MaskPredicate<?> m = roi.toMaskPredicate(plus);

		assertTrue(m instanceof RandomAccessibleIntervalAsMaskInterval);
	}

	@Test
	public void testMultiCallConversion() {
		final RealRandomAccessibleRealInterval<BoolType> rrari = roi
			.toRealRandomAccessibleRealInterval(new double[] { 12, 13 });

		// ensure two conversions happened as expected
		assertTrue(
			rrari instanceof RealMaskRealIntervalAsRealRandomAccessibleRealInterval);
		final RealMaskRealIntervalAsRealRandomAccessibleRealInterval<?> adapt =
			(RealMaskRealIntervalAsRealRandomAccessibleRealInterval<?>) rrari;
		assertTrue(adapt.getSource() instanceof PointMask);
	}

	@Test
	public void testCastingConversion() {
		final Sphere s = new ClosedWritableSphere(new double[] { 16.5, -0.25, 4 },
			8);
		final RealMask rm = roi.toRealMask(s);
		assertTrue(s == rm);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullConversion() {
		roi.toRealRandomAccessible(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidConversion() {
		final Box b = new OpenWritableBox(new double[] { 8, -2, 0.5, 105 },
			new double[] { 22, 65.25, 9, 107 });
		roi.toMaskInterval(b);
	}

	// -- Test Geometric MaskPredicate Creation --

	@Test
	public void testClosedBox() {
		final WritableBox wb = roi.closedBox(new double[] { 1.5, 3 }, new double[] {
			20, 32 });

		assertEquals(BoundaryType.CLOSED, wb.boundaryType());
		assertEquals(10.75, wb.center().getDoublePosition(0), 0);
		assertEquals(17.5, wb.center().getDoublePosition(1), 0);
		assertEquals(18.5, wb.sideLength(0), 0);
		assertEquals(29, wb.sideLength(1), 0);
	}

	@Test
	public void testOpenBox() {
		final WritableBox wb = roi.openBox(new double[] { 22, 34 }, new double[] {
			50, 102.5 });

		assertEquals(BoundaryType.OPEN, wb.boundaryType());
		assertEquals(36, wb.center().getDoublePosition(0), 0);
		assertEquals(68.25, wb.center().getDoublePosition(1), 0);
		assertEquals(28, wb.sideLength(0), 0);
		assertEquals(68.5, wb.sideLength(1), 0);
	}

	@Test
	public void testClosedEllipsoid() {
		final WritableEllipsoid we = roi.closedEllipsoid(new double[] { 19.5, 60 },
			new double[] { 5, 6.5 });

		assertEquals(BoundaryType.CLOSED, we.boundaryType());
		assertEquals(19.5, we.center().getDoublePosition(0), 0);
		assertEquals(60, we.center().getDoublePosition(1), 0);
		assertEquals(5, we.semiAxisLength(0), 0);
		assertEquals(6.5, we.semiAxisLength(1), 0);
	}

	@Test
	public void testOpenEllipsoid() {
		final WritableEllipsoid we = roi.openEllipsoid(new double[] { 33, 84 },
			new double[] { 7, 11.5 });

		assertEquals(BoundaryType.OPEN, we.boundaryType());
		assertEquals(33, we.center().getDoublePosition(0), 0);
		assertEquals(84, we.center().getDoublePosition(1), 0);
		assertEquals(7, we.semiAxisLength(0), 0);
		assertEquals(11.5, we.semiAxisLength(1), 0);
	}

	@Test
	public void testLineRealLocalizable() {
		final WritableLine wl = roi.line(new RealPoint(new double[] { 2, 3, 4 }),
			new RealPoint(new double[] { 13, 6, 22.5 }));

		assertEquals(BoundaryType.CLOSED, wl.boundaryType());
		assertEquals(2, wl.endpointOne().getDoublePosition(0), 0);
		assertEquals(3, wl.endpointOne().getDoublePosition(1), 0);
		assertEquals(4, wl.endpointOne().getDoublePosition(2), 0);
		assertEquals(13, wl.endpointTwo().getDoublePosition(0), 0);
		assertEquals(6, wl.endpointTwo().getDoublePosition(1), 0);
		assertEquals(22.5, wl.endpointTwo().getDoublePosition(2), 0);
	}

	@Test
	public void testLineDoubleArray() {
		final WritableLine wl = roi.line(new double[] { 0.5, 1 }, new double[] { 22,
			11 }, false);

		assertEquals(BoundaryType.CLOSED, wl.boundaryType());
		assertEquals(0.5, wl.endpointOne().getDoublePosition(0), 0);
		assertEquals(1, wl.endpointOne().getDoublePosition(1), 0);
		assertEquals(22, wl.endpointTwo().getDoublePosition(0), 0);
		assertEquals(11, wl.endpointTwo().getDoublePosition(1), 0);
	}

	@Test
	public void testPointMaskRealLocalizable() {
		final WritablePointMask wpm = roi.pointMask(new RealPoint(new double[] { 10,
			12, 19, 5 }));

		assertEquals(BoundaryType.CLOSED, wpm.boundaryType());
		assertEquals(10, wpm.getDoublePosition(0), 0);
		assertEquals(12, wpm.getDoublePosition(1), 0);
		assertEquals(19, wpm.getDoublePosition(2), 0);
		assertEquals(5, wpm.getDoublePosition(3), 0);
	}

	@Test
	public void testPointMaskDoubleArray() {
		final WritablePointMask wpm = roi.pointMask(new double[] { 0.25, 3 });

		assertEquals(BoundaryType.CLOSED, wpm.boundaryType());
		assertEquals(0.25, wpm.getDoublePosition(0), 0);
		assertEquals(3, wpm.getDoublePosition(1), 0);
	}

	@Test
	public void testPolygon2DList() {
		final List<RealLocalizable> pts = new ArrayList<>(3);
		pts.add(new RealPoint(new double[] { 42, 93 }));
		pts.add(new RealPoint(new double[] { 50, 50.5 }));
		pts.add(new RealPoint(new double[] { 58, 93 }));

		final WritablePolygon2D wp = roi.polygon2D(pts);

		assertEquals(BoundaryType.UNSPECIFIED, wp.boundaryType());
		assertEquals(3, wp.numVertices());
		assertEquals(42, wp.vertex(0).getDoublePosition(0), 0);
		assertEquals(93, wp.vertex(0).getDoublePosition(1), 0);
		assertEquals(50, wp.vertex(1).getDoublePosition(0), 0);
		assertEquals(50.5, wp.vertex(1).getDoublePosition(1), 0);
		assertEquals(58, wp.vertex(2).getDoublePosition(0), 0);
		assertEquals(93, wp.vertex(2).getDoublePosition(1), 0);
	}

	@Test
	public void testPolygon2DArrays() {
		final WritablePolygon2D wp = roi.polygon2D(new double[] { 13, 16, 22, 15 },
			new double[] { 17, 22.5, 40, 33 });

		assertEquals(BoundaryType.UNSPECIFIED, wp.boundaryType());
		assertEquals(4, wp.numVertices());
		assertEquals(13, wp.vertex(0).getDoublePosition(0), 0);
		assertEquals(17, wp.vertex(0).getDoublePosition(1), 0);
		assertEquals(16, wp.vertex(1).getDoublePosition(0), 0);
		assertEquals(22.5, wp.vertex(1).getDoublePosition(1), 0);
		assertEquals(22, wp.vertex(2).getDoublePosition(0), 0);
		assertEquals(40, wp.vertex(2).getDoublePosition(1), 0);
		assertEquals(15, wp.vertex(3).getDoublePosition(0), 0);
		assertEquals(33, wp.vertex(3).getDoublePosition(1), 0);
	}

	@Test
	public void testClosedPolygon2DList() {
		final List<RealLocalizable> pts = new ArrayList<>(5);
		pts.add(new RealPoint(new double[] { 0, 0 }));
		pts.add(new RealPoint(new double[] { 3, 13 }));
		pts.add(new RealPoint(new double[] { 6, 15 }));
		pts.add(new RealPoint(new double[] { 6, 22 }));
		pts.add(new RealPoint(new double[] { 3, 5 }));

		final WritablePolygon2D wp = roi.closedPolygon2D(pts);

		assertEquals(BoundaryType.CLOSED, wp.boundaryType());
		assertEquals(5, wp.numVertices());
		assertEquals(0, wp.vertex(0).getDoublePosition(0), 0);
		assertEquals(0, wp.vertex(0).getDoublePosition(1), 0);
		assertEquals(3, wp.vertex(1).getDoublePosition(0), 0);
		assertEquals(13, wp.vertex(1).getDoublePosition(1), 0);
		assertEquals(6, wp.vertex(2).getDoublePosition(0), 0);
		assertEquals(15, wp.vertex(2).getDoublePosition(1), 0);
		assertEquals(6, wp.vertex(3).getDoublePosition(0), 0);
		assertEquals(22, wp.vertex(3).getDoublePosition(1), 0);
		assertEquals(3, wp.vertex(4).getDoublePosition(0), 0);
		assertEquals(5, wp.vertex(4).getDoublePosition(1), 0);
	}

	@Test
	public void testClosedPolygon2DArrays() {
		final WritablePolygon2D wp = roi.closedPolygon2D(new double[] { 2, 8, 9 },
			new double[] { 0.5, 32, 6 });

		assertEquals(BoundaryType.CLOSED, wp.boundaryType());
		assertEquals(3, wp.numVertices());
		assertEquals(2, wp.vertex(0).getDoublePosition(0), 0);
		assertEquals(0.5, wp.vertex(0).getDoublePosition(1), 0);
		assertEquals(8, wp.vertex(1).getDoublePosition(0), 0);
		assertEquals(32, wp.vertex(1).getDoublePosition(1), 0);
		assertEquals(9, wp.vertex(2).getDoublePosition(0), 0);
		assertEquals(6, wp.vertex(2).getDoublePosition(1), 0);
	}

	@Test
	public void testOpenPolygon2DList() {
		final List<RealLocalizable> pts = new ArrayList<>(3);
		pts.add(new RealPoint(new double[] { 42, 93 }));
		pts.add(new RealPoint(new double[] { 50, 50.5 }));
		pts.add(new RealPoint(new double[] { 58, 93 }));

		final WritablePolygon2D wp = roi.openPolygon2D(pts);

		assertEquals(BoundaryType.OPEN, wp.boundaryType());
		assertEquals(3, wp.numVertices());
		assertEquals(42, wp.vertex(0).getDoublePosition(0), 0);
		assertEquals(93, wp.vertex(0).getDoublePosition(1), 0);
		assertEquals(50, wp.vertex(1).getDoublePosition(0), 0);
		assertEquals(50.5, wp.vertex(1).getDoublePosition(1), 0);
		assertEquals(58, wp.vertex(2).getDoublePosition(0), 0);
		assertEquals(93, wp.vertex(2).getDoublePosition(1), 0);
	}

	@Test
	public void testOpenPolygon2DArrays() {
		final WritablePolygon2D wp = roi.openPolygon2D(new double[] { 0.5, 0,
			0.125 }, new double[] { 3, 3.5, 2 });

		assertEquals(BoundaryType.OPEN, wp.boundaryType());
		assertEquals(3, wp.numVertices());
		assertEquals(0.5, wp.vertex(0).getDoublePosition(0), 0);
		assertEquals(3, wp.vertex(0).getDoublePosition(1), 0);
		assertEquals(0, wp.vertex(1).getDoublePosition(0), 0);
		assertEquals(3.5, wp.vertex(1).getDoublePosition(1), 0);
		assertEquals(0.125, wp.vertex(2).getDoublePosition(0), 0);
		assertEquals(2, wp.vertex(2).getDoublePosition(1), 0);
	}

	@Test
	public void testPolyline() {
		final List<RealLocalizable> pts = new ArrayList<>(4);
		pts.add(new RealPoint(new double[] { 2, 3.5, 6 }));
		pts.add(new RealPoint(new double[] { 20, 4, 5.5 }));
		pts.add(new RealPoint(new double[] { 16, 22, 30 }));
		pts.add(new RealPoint(new double[] { 23, 0.5, 9 }));

		final WritablePolyline wp = roi.polyline(pts);

		assertEquals(BoundaryType.CLOSED, wp.boundaryType());
		assertEquals(4, wp.numVertices());
		assertEquals(2, wp.vertex(0).getDoublePosition(0), 0);
		assertEquals(3.5, wp.vertex(0).getDoublePosition(1), 0);
		assertEquals(6, wp.vertex(0).getDoublePosition(2), 0);
		assertEquals(20, wp.vertex(1).getDoublePosition(0), 0);
		assertEquals(4, wp.vertex(1).getDoublePosition(1), 0);
		assertEquals(5.5, wp.vertex(1).getDoublePosition(2), 0);
		assertEquals(16, wp.vertex(2).getDoublePosition(0), 0);
		assertEquals(22, wp.vertex(2).getDoublePosition(1), 0);
		assertEquals(30, wp.vertex(2).getDoublePosition(2), 0);
		assertEquals(23, wp.vertex(3).getDoublePosition(0), 0);
		assertEquals(0.5, wp.vertex(3).getDoublePosition(1), 0);
		assertEquals(9, wp.vertex(3).getDoublePosition(2), 0);
	}

	@Test
	public void testRealPointCollectionHashMap() {
		final HashMap<TDoubleArrayList, RealLocalizable> pts = new HashMap<>();
		final double[] pOne = new double[2];
		final double[] pTwo = new double[2];
		final double[] pThree = new double[2];
		points.get(0).localize(pOne);
		points.get(1).localize(pTwo);
		points.get(2).localize(pThree);
		pts.put(new TDoubleArrayList(pOne), new RealPoint(points.get(0)));
		pts.put(new TDoubleArrayList(pTwo), new RealPoint(points.get(1)));
		pts.put(new TDoubleArrayList(pThree), new RealPoint(points.get(2)));

		final WritableRealPointCollection<RealLocalizable> wrpc = roi
			.realPointCollection(pts);
		assertRealPointCollectionCorrect(wrpc);
	}

	@Test
	public void testRealPointCollectionCollection() {
		final WritableRealPointCollection<RealLocalizable> wrpc = roi
			.realPointCollection(points);
		assertRealPointCollectionCorrect(wrpc);
	}

	@Test
	public void testKDTreeRealPointCollectionKDTree() {
		final KDTree<RealLocalizable> kdtree = new KDTree<>(points, points);
		final RealPointCollection<RealLocalizable> rpc = roi
			.kDTreeRealPointCollection(kdtree);

		assertFalse(rpc instanceof WritableRealPointCollection);
		assertRealPointCollectionCorrect(rpc);
	}

	@Test
	public void testKDTreeRealPointCollectionCollection() {
		final RealPointCollection<RealLocalizable> rpc = roi
			.kDTreeRealPointCollection(points);

		assertFalse(rpc instanceof WritableRealPointCollection);
		assertRealPointCollectionCorrect(rpc);
	}

	@Test
	public void testRealPointSampleListWritableRealPointCollectionRPSL() {
		final RealPointSampleList<RealLocalizable> rpsl = new RealPointSampleList<>(
			2);
		rpsl.add((RealPoint) points.get(0), points.get(0));
		rpsl.add((RealPoint) points.get(1), points.get(1));
		rpsl.add((RealPoint) points.get(2), points.get(2));

		final WritableRealPointCollection<RealLocalizable> wrpc = roi
			.realPointSampleListRealPointCollection(rpsl);
		assertRealPointCollectionCorrect(wrpc);
	}

	@Test
	public void testRealPointSampleListWritableRealPointCollectionCollection() {
		final WritableRealPointCollection<RealLocalizable> wrpc = roi
			.realPointSampleListRealPointCollection(points);
		assertRealPointCollectionCorrect(wrpc);
	}

	@Test
	public void testClosedSphere() {
		final WritableSphere s = roi.closedSphere(new double[] { 3, 0.5, 6 }, 4);

		assertEquals(BoundaryType.CLOSED, s.boundaryType());
		assertEquals(3, s.center().getDoublePosition(0), 0);
		assertEquals(0.5, s.center().getDoublePosition(1), 0);
		assertEquals(6, s.center().getDoublePosition(2), 0);
		assertEquals(4, s.radius(), 0);
	}

	@Test
	public void testOpenSphere() {
		final WritableSphere s = roi.openSphere(new double[] { 10.5, 4 }, 0.25);

		assertEquals(BoundaryType.OPEN, s.boundaryType());
		assertEquals(10.5, s.center().getDoublePosition(0), 0);
		assertEquals(4, s.center().getDoublePosition(1), 0);
		assertEquals(0.25, s.radius(), 0);
	}

	@Test
	public void testClosedSuperEllipsoid() {
		final WritableSuperEllipsoid se = roi.closedSuperEllipsoid(new double[] { 7,
			22, 4.5 }, new double[] { 3, 6, 1 }, 4);

		assertEquals(BoundaryType.CLOSED, se.boundaryType());
		assertEquals(7, se.center().getDoublePosition(0), 0);
		assertEquals(22, se.center().getDoublePosition(1), 0);
		assertEquals(4.5, se.center().getDoublePosition(2), 0);
		assertEquals(3, se.semiAxisLength(0), 0);
		assertEquals(6, se.semiAxisLength(1), 0);
		assertEquals(1, se.semiAxisLength(2), 0);
		assertEquals(4, se.exponent(), 0);
	}

	@Test
	public void testOpenSuperEllipsoid() {
		final WritableSuperEllipsoid se = roi.openSuperEllipsoid(new double[] { 11,
			2, 43, -4 }, new double[] { 2, 7, 0.5, 9 }, 0.5);

		assertEquals(BoundaryType.OPEN, se.boundaryType());
		assertEquals(11, se.center().getDoublePosition(0), 0);
		assertEquals(2, se.center().getDoublePosition(1), 0);
		assertEquals(43, se.center().getDoublePosition(2), 0);
		assertEquals(-4, se.center().getDoublePosition(3), 0);
		assertEquals(2, se.semiAxisLength(0), 0);
		assertEquals(7, se.semiAxisLength(1), 0);
		assertEquals(0.5, se.semiAxisLength(2), 0);
		assertEquals(9, se.semiAxisLength(3), 0);
		assertEquals(0.5, se.exponent(), 0);
	}

	// -- Helper methods --

	private void assertRealPointCollectionCorrect(
		final RealPointCollection<RealLocalizable> rpc)
	{
		final Iterable<RealLocalizable> it = rpc.points();

		for (final RealLocalizable pt : it) {
			assertTrue(realLocalizableEquals(pt));
		}
	}

	private boolean realLocalizableEquals(final RealLocalizable pt) {
		for (final RealLocalizable p : points) {
			if (p.getDoublePosition(0) == pt.getDoublePosition(0) && p
				.getDoublePosition(1) == pt.getDoublePosition(1)) return true;
		}
		return false;
	}

	// -- Helper classes --

	@Plugin(type = Converter.class, priority = Priority.LAST)
	public static final class DoubleArrayToPointMaskConverter extends
		AbstractConverter<double[], PointMask>
	{

		@Override
		@SuppressWarnings("unchecked")
		public <T> T convert(final Object src, final Class<T> dest) {
			return (T) new DefaultWritablePointMask((double[]) src);
		}

		@Override
		public Class<PointMask> getOutputType() {
			return PointMask.class;
		}

		@Override
		public Class<double[]> getInputType() {
			return double[].class;
		}

	}
}
