package net.imagej.convert;

import net.imagej.roi.DefaultROITree;
import net.imagej.roi.ROITree;
import net.imglib2.roi.geom.real.ClosedWritableEllipsoid;
import net.imglib2.roi.geom.real.Ellipsoid;
import net.imglib2.roi.geom.real.SuperEllipsoid;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.scijava.Context;
import org.scijava.convert.ConvertService;

import java.util.Collections;

public class ConvertROITreeToMaskTest
{

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

	@Test
	public void testROITreeToSuperEllipsoidTest() {
		// Construct the ROI tree
		Ellipsoid expected = new ClosedWritableEllipsoid(new double[] {0, 0}, new double[] {5, 10} );
		ROITree tree = new DefaultROITree();
		tree.addROIs( Collections.singletonList(expected) );
		// Assert that the convertService can convert this tree to a mask
		final ConvertService convertService = c.service(ConvertService.class);
		SuperEllipsoid actual = convertService.convert( tree, SuperEllipsoid.class );
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testTooFullROITree() {
		// Construct the ROI tree
		Ellipsoid expected = new ClosedWritableEllipsoid(new double[] {0, 0}, new double[] {5, 10} );
		Ellipsoid invader = new ClosedWritableEllipsoid(new double[] {10, 10}, new double[] {5, 10} );
		ROITree tree = new DefaultROITree();
		tree.addROIs( Collections.singletonList(expected) );
		tree.addROIs( Collections.singletonList(invader) );
		// Assert that the convertService cannot convert this tree to a mask
		final ConvertService convertService = c.service(ConvertService.class);
		Assert.assertNull(convertService.convert( tree, SuperEllipsoid.class ));
	}
}
