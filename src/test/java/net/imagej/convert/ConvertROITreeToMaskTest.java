
package net.imagej.convert;

import net.imagej.roi.DefaultROITree;
import net.imagej.roi.ROITree;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.roi.MaskPredicate;
import net.imglib2.roi.geom.real.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scijava.Context;
import org.scijava.convert.ConvertService;
import org.scijava.util.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Test {@link ROITreeToMaskPredicateConverter} and
 * {@link ROITreeToMaskPredicateConverters}.
 * 
 * @author Gabriel Selzer
 */
@RunWith(Parameterized.class)
public class ConvertROITreeToMaskTest {

	private Context c;

	@Before
	public void setUp() {
		c = new Context(ConvertService.class);
	}

	@After
	public void tearDown() {
		c.dispose();
		c = null;
	}

	@Parameterized.Parameter
	public MaskPredicate<?> ROI;

	@Parameterized.Parameters(name = "{index}: ROI - {0}")
	public static Object[] data() {
		return new Object[] { //
			new ClosedWritableEllipsoid( //
				new double[] { 0, 0 }, //
				new double[] { 5, 10 } //
			), //
			new ClosedWritableBox( //
				new double[] { 0, 0 }, //
				new double[] { 5, 10 } //
			), //
			new ClosedWritablePolygon2D( //
				new double[] { 0, 3, 4 }, //
				new double[] { 0, 0, 4 } //
			), //
			new DefaultWritableLine( //
				new double[] { 0, 0 }, //
				new double[] { 5, 5 }, //
				true), //
			new DefaultWritablePolyline(Arrays.asList( //
				new RealPoint(0, 0), //
				new RealPoint(1, 1), //
				new RealPoint(2, 0) //
			)) //
		};
	}

	@Test
	public void testROITreeToROITest() {
		// Construct the ROI tree
		ROITree tree = new DefaultROITree();
		tree.addROIs(Collections.singletonList(ROI));
		// Assert that the convertService can convert this tree to a mask
		final ConvertService convertService = c.service(ConvertService.class);
		MaskPredicate<?> actual = convertService.convert(tree, ROI.getClass());
		Assert.assertEquals(ROI, actual);
	}

	@Test
	public void testROIToROITreeTest() {
		// Assert that the convertService can convert this mask to a tree
		final ConvertService convertService = c.service(ConvertService.class);
		ROITree actual = convertService.convert(ROI, ROITree.class);
		Assert.assertNotNull(actual);
		List<TreeNode<?>> rois = actual.children();
		Assert.assertEquals(1, rois.size());
		// Assert roi equality
		Assert.assertEquals(ROI, rois.get(0).data());
	}

	@Test
	public void testTooFullROITree() {
		// Construct the ROI tree
		ROITree tree = new DefaultROITree();
		tree.addROIs(Arrays.asList(ROI, ROI));
		// Assert that the convertService cannot convert this tree to a mask
		final ConvertService convertService = c.service(ConvertService.class);
		Assert.assertNull(convertService.convert(tree, ROI.getClass()));
	}

}
