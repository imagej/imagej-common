
package net.imagej.convert;

import net.imagej.roi.DefaultROITree;
import net.imagej.roi.ROITree;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.roi.MaskPredicate;
import net.imglib2.roi.RealMask;
import net.imglib2.roi.geom.real.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scijava.Context;
import org.scijava.convert.ConvertService;
import org.scijava.util.TreeNode;

import java.util.*;
import java.util.function.Predicate;

/**
 * Test {@link ROITreeToMaskPredicateConverter} and
 * {@link MaskPredicateToROITreeConverter}.
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

	// The first index in data
	@Parameterized.Parameter
	public MaskPredicate<?> roi;

	// The second index in data
	@Parameterized.Parameter(1)
	public Class<MaskPredicate<?>> roiClass;

	@Parameterized.Parameters(name = "{index}: ROITree <-> {1}")
	public static Collection<Object[]> data() {
		return Arrays.asList( //
			new Object[] { //
				new ClosedWritableEllipsoid( //
					new double[] { 0, 0 }, //
					new double[] { 5, 10 } //
				), //
				ClosedWritableEllipsoid.class //
			}, //
			new Object[] { //
				new OpenWritableEllipsoid( //
					new double[] { 0, 0 }, //
					new double[] { 5, 10 } //
				), //
				OpenWritableEllipsoid.class //
			}, //
			new Object[] { //
				new ClosedWritableSphere( //
					new double[] { 0, 0 }, //
					10 //
				), //
				ClosedWritableSphere.class //
			}, //
			new Object[] { //
				new OpenWritableSphere( //
					new double[] { 0, 0 }, //
					10 //
				), //
				OpenWritableSphere.class //
			}, //
			new Object[] { //
				new ClosedWritableBox( //
					new double[] { 0, 0 }, //
					new double[] { 5, 10 } //
				), //
				ClosedWritableBox.class //
			}, //
			new Object[] { //
				new OpenWritableBox( //
					new double[] { 0, 0 }, //
					new double[] { 5, 10 } //
				), //
				OpenWritableBox.class //
			}, //
			new Object[] { //
				new ClosedWritablePolygon2D( //
					new double[] { 0, 3, 4 }, //
					new double[] { 0, 0, 4 } //
				), //
				ClosedWritablePolygon2D.class //
			}, //
			new Object[] { //
				new OpenWritablePolygon2D( //
					new double[] { 0, 3, 4 }, //
					new double[] { 0, 0, 4 } //
				), //
				OpenWritablePolygon2D.class //
			}, //
			new Object[] { //
				new DefaultWritableLine( //
					new double[] { 0, 0 }, //
					new double[] { 5, 5 }, //
					true //
				), //
				DefaultWritableLine.class, //
			}, //
			new Object[] { //
				new DefaultWritablePolyline(Arrays.asList( //
					new RealPoint(0, 0), //
					new RealPoint(1, 1), //
					new RealPoint(2, 0) //
				)), //
				DefaultWritablePolyline.class, //
			}, //
			new Object[] { //
				new RealMask()
				{

					@Override
					public boolean test(RealLocalizable realLocalizable) {
						return realLocalizable.getDoublePosition(0) + realLocalizable
							.getDoublePosition(1) == 0;
					}

					@Override
					public int numDimensions() {
						return 2;
					}
				}, //
				RealMask.class //
			}, //
			new Object[] { //
				new MaskPredicate<RealLocalizable>()
				{

					@Override
					public int numDimensions() {
						return 2;
					}

					@Override
					public boolean test(RealLocalizable realLocalizable) {
						// true iff any coordinate is zero
						return Arrays //
							.stream(realLocalizable.positionAsDoubleArray()) //
							.anyMatch(d -> d == 0);
					}

					@Override
					public MaskPredicate<RealLocalizable> and(
						Predicate<? super RealLocalizable> other)
				{
						throw new UnsupportedOperationException();
					}

					@Override
					public MaskPredicate<RealLocalizable> or(
						Predicate<? super RealLocalizable> other)
				{
						throw new UnsupportedOperationException();
					}

					@Override
					public MaskPredicate<RealLocalizable> negate() {
						throw new UnsupportedOperationException();
					}

					@Override
					public MaskPredicate<RealLocalizable> minus(
						Predicate<? super RealLocalizable> other)
				{
						throw new UnsupportedOperationException();
					}

					@Override
					public MaskPredicate<RealLocalizable> xor(
						Predicate<? super RealLocalizable> other)
				{
						throw new UnsupportedOperationException();
					}
				}, //
				MaskPredicate.class //
			} //
		);
	}

	@Test
	public void testROITreeToROITest() {
		// Construct the ROI tree
		ROITree tree = new DefaultROITree();
		tree.addROIs(Collections.singletonList(roi));
		// Assert that the convertService can convert this tree to a mask
		final ConvertService convertService = c.service(ConvertService.class);
		MaskPredicate<?> actual = convertService.convert(tree, roiClass);
		Assert.assertEquals(roi, actual);
	}

	@Test
	public void testROIToROITreeTest() {
		// Assert that the convertService can convert this mask to a tree
		final ConvertService convertService = c.service(ConvertService.class);
		ROITree actual = convertService.convert(roi, ROITree.class);
		Assert.assertNotNull(actual);
		List<TreeNode<?>> rois = actual.children();
		Assert.assertEquals(1, rois.size());
		// Assert roi equality
		Assert.assertEquals(roi, rois.get(0).data());
	}

	@Test
	public void testTooFullROITree() {
		// Construct the ROI tree
		ROITree tree = new DefaultROITree();
		tree.addROIs(Arrays.asList(roi, roi));
		// Assert that the convertService cannot convert this tree to a mask
		final ConvertService convertService = c.service(ConvertService.class);
		Assert.assertNull(convertService.convert(tree, roi.getClass()));
	}

}
