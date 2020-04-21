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

package net.imagej.roi;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.imagej.Dataset;
import net.imagej.ImageJService;
import net.imglib2.KDTree;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPointSampleList;
import net.imglib2.RealRandomAccessible;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.roi.BoundaryType;
import net.imglib2.roi.Mask;
import net.imglib2.roi.MaskInterval;
import net.imglib2.roi.MaskPredicate;
import net.imglib2.roi.RealMask;
import net.imglib2.roi.RealMaskRealInterval;
import net.imglib2.roi.geom.GeomMasks;
import net.imglib2.roi.geom.real.RealPointCollection;
import net.imglib2.roi.geom.real.WritableBox;
import net.imglib2.roi.geom.real.WritableEllipsoid;
import net.imglib2.roi.geom.real.WritableLine;
import net.imglib2.roi.geom.real.WritablePointMask;
import net.imglib2.roi.geom.real.WritablePolygon2D;
import net.imglib2.roi.geom.real.WritablePolyline;
import net.imglib2.roi.geom.real.WritableRealPointCollection;
import net.imglib2.roi.geom.real.WritableSphere;
import net.imglib2.roi.geom.real.WritableSuperEllipsoid;
import net.imglib2.type.logic.BoolType;

import org.scijava.service.Service;
import org.scijava.util.DefaultTreeNode;
import org.scijava.util.TreeNode;

import gnu.trove.list.array.TDoubleArrayList;

/**
 * {@link Service} for working with Regions of Interest (ROI).
 *
 * @author Alison Walter
 */
public interface ROIService extends ImageJService {

	public static final String ROI_PROPERTY = "rois";

	/**
	 * Returns the {@link ROITree} of the given {@link Dataset}
	 *
	 * @param img {@link Dataset} whose associated ROIs will be returned
	 * @return {@link ROITree} whose children are the associated ROIs as
	 *         {@link TreeNode}s
	 */
	default ROITree getROIs(final Dataset img) {
		final Object o = img.getProperties().get(ROI_PROPERTY);
		return o instanceof ROITree ? (ROITree) o : null;
	}

	/**
	 * Attaches the given ROI to the given image
	 *
	 * @param roi ROI (most likely {@link MaskPredicate}) to be attached
	 * @param img image to attach ROI to
	 */
	default void add(final Object roi, final Object img) {
		if (!(roi instanceof MaskPredicate)) throw new IllegalArgumentException(roi
			.getClass() + " is not a supported ROI type");
		if (!(img instanceof Dataset)) throw new IllegalArgumentException(img
			.getClass() + " is not a supported image type");
		final Dataset d = (Dataset) img;
		final MaskPredicate<?> mp = (MaskPredicate<?>) roi;
		if (d.getProperties().get(ROI_PROPERTY) != null) {
			final ROITree rp = (ROITree) d.getProperties().get(ROI_PROPERTY);
			rp.children().add(new DefaultTreeNode<>(mp, rp));
		}
		else {
			final ROITree rp = new DefaultROITree();
			rp.addROIs(Collections.singletonList(mp));
			d.getProperties().put(ROI_PROPERTY, rp);
		}
	}

	/**
	 * Check if the given {@code Object} is or has ROIs
	 *
	 * @param o {@code Object} to check
	 * @return true if it has or is a ROI, false otherwise
	 */
	default boolean hasROIs(final Object o) {
		if (o instanceof Dataset) return ((Dataset) o).getProperties().get(
			ROI_PROPERTY) != null;
		return o instanceof MaskPredicate;
	}

	/**
	 * Clears all {@link MaskPredicate}s associated with the given {@link Dataset}
	 *
	 * @param img {@link Dataset} whose rois will be cleared
	 */
	default void clear(final Dataset img) {
		img.getProperties().put(ROI_PROPERTY, null);
	}

	// -- Object to (Real)Mask methods --

	/**
	 * Attempts to convert the given {@code Object} to a {@code Mask}
	 *
	 * @param o the Object to be converted. This object should be something like
	 *          an ImageJ 1.x Roi, RandomAccessible, etc.
	 * @return a {@link Mask} representation of the given object, if possible
	 */
	Mask toMask(Object o);

	/**
	 * Attempts to convert the given {@code Object} to a {@code MaskInterval}
	 *
	 * @param o the Object to be converted. This object should be something like
	 *          an ImageJ 1.x Roi, RandomAccessibleInterval, etc.
	 * @return a {@link MaskInterval} representation of the given object, if
	 *         possible
	 */
	MaskInterval toMaskInterval(Object o);

	/**
	 * Attempts to convert the given {@code Object} to a {@code RealMask}
	 *
	 * @param o the Object to be converted. This object should be something like
	 *          an ImageJ 1.x Roi, RealRandomAccessible, etc.
	 * @return a {@link RealMask} representation of the given object, if possible
	 */
	RealMask toRealMask(Object o);

	/**
	 * Attempts to convert the given {@code Object} to a
	 * {@code RealMaskRealInterval}
	 *
	 * @param o the Object to be converted. This object should be something like
	 *          an ImageJ 1.x Roi, RealRandomAccessibleRealInterval, etc.
	 * @return a {@link RealMaskRealInterval} representation of the given object,
	 *         if possible
	 */
	RealMaskRealInterval toRealMaskRealInterval(Object o);

	/**
	 * Attempts to convert the given {@code Object} to a {@link MaskPredicate}.
	 *
	 * @param o the Object to be converted. This object should be something like
	 *          an ImageJ 1.x Roi, (Real)RandomAccessible, etc.
	 * @return a {@link MaskPredicate} representation of the given Object, if
	 *         possible
	 */
	MaskPredicate<?> toMaskPredicate(Object o);

	// -- Object to (Real)RandomAccessible methods --

	/**
	 * Attempts to convert the given {@code Object} to a
	 * {@code RandomAccessible<BoolType>}; otherwise, an exception is thrown.
	 *
	 * @param o the Object to be converted. In general, this should be an ImageJ
	 *          1.x Roi or an Imglib2 MaskPredicate.
	 * @return a RandomAccessible representation of the given Object
	 */
	RandomAccessible<BoolType> toRandomAcessible(Object o);

	/**
	 * Attempts to convert the given {@code Object} to a
	 * {@code RandomAccessibleInterval<BoolType>}; otherwise, an exception is
	 * thrown.
	 *
	 * @param o the Object to be converted. In general, this should be an ImageJ
	 *          1.x Roi or an ImgLib2 MaskPredicate.
	 * @return a RandomAccessibleInterval representation of the given Object
	 */
	RandomAccessibleInterval<BoolType> toRandomAccessibleInterval(Object o);

	/**
	 * Attempts to convert the given {@code Object} to a
	 * {@link RealRandomAccessible}.
	 *
	 * @param o the Object to be converted. In general, this should be an ImageJ
	 *          1.x Roi or an ImgLib2 MaskPredicate.
	 * @return a RealRandomAccessible representation of the given Object
	 */
	RealRandomAccessible<BoolType> toRealRandomAccessible(Object o);

	/**
	 * Attempts to convert the given {@code Object} to a
	 * {@link RealRandomAccessibleRealInterval}.
	 *
	 * @param o the Object to be converted. In general, this should be an ImageJ
	 *          1.x Roi or an ImgLib2 MaskPredicate.
	 * @return a RealRandomAccessibleRealInterval representation of the given
	 *         Object
	 */
	RealRandomAccessibleRealInterval<BoolType> toRealRandomAccessibleRealInterval(
		Object o);

	// -- Create Geometric MaskPredicate methods --

	// ---- Box ----

	/**
	 * Creates a {@link WritableBox} with {@link BoundaryType#CLOSED closed}
	 * boundary behavior.
	 *
	 * @param min the minimum position of the Box in each dimension
	 * @param max the maximum position of the Box in each dimension
	 * @return a {@link WritableBox} with the given min/max
	 */
	default WritableBox closedBox(final double[] min, final double[] max) {
		return GeomMasks.closedBox(min, max);
	}

	/**
	 * Creates a {@link WritableBox} with {@link BoundaryType#OPEN open} boundary
	 * behavior.
	 *
	 * @param min the minimum position of the Box in each dimension
	 * @param max the maximum position of the Box in each dimension
	 * @return a {@link WritableBox} with the given min/max
	 */
	default WritableBox openBox(final double[] min, final double[] max) {
		return GeomMasks.openBox(min, max);
	}

	// ---- Ellipsoid ----

	/**
	 * Creates an {@link WritableEllipsoid} with {@link BoundaryType#CLOSED
	 * closed} boundary behavior.
	 *
	 * @param center the position of the ellipsoid's center
	 * @param semiAxisLengths the extension of the ellipsoid in each dimension
	 * @return an ellipsoid positioned at the given center with corresponding semi
	 *         axis lengths
	 */
	default WritableEllipsoid closedEllipsoid(final double[] center,
		final double[] semiAxisLengths)
	{
		return GeomMasks.closedEllipsoid(center, semiAxisLengths);
	}

	/**
	 * Creates an {@link WritableEllipsoid} with {@link BoundaryType#OPEN open}
	 * boundary behavior.
	 *
	 * @param center the position of the ellipsoid's center
	 * @param semiAxisLengths the extension of the ellipsoid in each dimension
	 * @return an ellipsoid positioned at the given center with corresponding semi
	 *         axis lengths
	 */
	default WritableEllipsoid openEllipsoid(final double[] center,
		final double[] semiAxisLengths)
	{
		return GeomMasks.openEllipsoid(center, semiAxisLengths);
	}

	// ---- Line ----

	/**
	 * Creates a {@link WritableLine} with the given endpoints.
	 *
	 * @param pointOne position of the first endpoint of the line
	 * @param pointTwo position of the second endpoint of the line
	 * @return a line with the given endpoints
	 */
	default WritableLine line(final RealLocalizable pointOne,
		final RealLocalizable pointTwo)
	{
		return GeomMasks.line(pointOne, pointTwo);
	}

	/**
	 * Creates a {@link WritableLine} with the given endpoints.
	 *
	 * @param pointOne position of the first endpoint of the line
	 * @param pointTwo position of the second endpoint of the line
	 * @param copy if true create copies are created and stored of the two arrays,
	 *          if false arrays are just stored
	 * @return a line with the given endpoints
	 */
	default WritableLine line(final double[] pointOne, final double[] pointTwo,
		final boolean copy)
	{
		return GeomMasks.line(pointOne, pointTwo, copy);
	}

	// ---- Point ----

	/**
	 * Creates a {@link WritablePointMask} at the given location.
	 *
	 * @param point location of the point
	 * @return a point at the given location
	 */
	default WritablePointMask pointMask(final double[] point) {
		return GeomMasks.pointMask(point);
	}

	/**
	 * Creates a {@link WritablePointMask} at the given location.
	 *
	 * @param point location of the point
	 * @return a point at the given location
	 */
	default WritablePointMask pointMask(final RealLocalizable point) {
		return GeomMasks.pointMask(point);
	}

	// ---- Polygon2D ----

	/**
	 * Creates a {@link WritablePolygon2D} with {@link BoundaryType#UNSPECIFIED
	 * unspecified} boundary behavior.
	 *
	 * @param vertices a list containing the location of each vertex in 2D space
	 * @return a 2D polygon with the given vertices
	 */
	default WritablePolygon2D polygon2D(
		final List<? extends RealLocalizable> vertices)
	{
		return GeomMasks.polygon2D(vertices);
	}

	/**
	 * Creates a {@link WritablePolygon2D} with {@link BoundaryType#UNSPECIFIED
	 * unspecified} boundary behavior.
	 *
	 * @param x the x-coordinates of the vertices
	 * @param y the y-coordinates of the vertices
	 * @return a 2D polygon with the given vertices
	 */
	default WritablePolygon2D polygon2D(final double[] x, final double[] y) {
		return GeomMasks.polygon2D(x, y);
	}

	/**
	 * Creates a {@link WritablePolygon2D} with {@link BoundaryType#CLOSED closed}
	 * boundary behavior.
	 *
	 * @param vertices a list containing the location of each vertex in 2D space
	 * @return a 2D polygon with the given vertices
	 */
	default WritablePolygon2D closedPolygon2D(
		final List<? extends RealLocalizable> vertices)
	{
		return GeomMasks.closedPolygon2D(vertices);
	}

	/**
	 * Creates a {@link WritablePolygon2D} with {@link BoundaryType#CLOSED closed}
	 * boundary behavior.
	 *
	 * @param x the x-coordinates of the vertices
	 * @param y the y-coordinates of the vertices
	 * @return a 2D polygon with the given vertices
	 */
	default WritablePolygon2D closedPolygon2D(final double[] x,
		final double[] y)
	{
		return GeomMasks.closedPolygon2D(x, y);
	}

	/**
	 * Creates a {@link WritablePolygon2D} with {@link BoundaryType#OPEN open}
	 * boundary behavior.
	 *
	 * @param vertices a list containing the location of each vertex in 2D space
	 * @return a 2D polygon with the given vertices
	 */
	default WritablePolygon2D openPolygon2D(
		final List<? extends RealLocalizable> vertices)
	{
		return GeomMasks.openPolygon2D(vertices);
	}

	/**
	 * Creates a {@link WritablePolygon2D} with {@link BoundaryType#OPEN open}
	 * boundary behavior.
	 *
	 * @param x the x-coordinates of the vertices
	 * @param y the y-coordinates of the vertices
	 * @return a 2D polygon with the given vertices
	 */
	default WritablePolygon2D openPolygon2D(final double[] x, final double[] y) {
		return GeomMasks.openPolygon2D(x, y);
	}

	// ---- Polyline ----

	/**
	 * Creates a {@link WritablePolyline} containing the given vertices, in the
	 * given order.
	 *
	 * @param vertices contains the vertices of the polyline in order from start
	 *          to end
	 * @return a polyline with the given vertices
	 */
	default WritablePolyline polyline(
		final List<? extends RealLocalizable> vertices)
	{
		return GeomMasks.polyline(vertices);
	}

	// ---- RealPointCollection ----

	/**
	 * Creates a mutable {@link WritableRealPointCollection} containing the given
	 * points.
	 *
	 * @param points contains the locations of the points in this collection
	 * @return a {@link WritableRealPointCollection} containing the given points
	 */
	default <L extends RealLocalizable> WritableRealPointCollection<L>
		realPointCollection(final HashMap<TDoubleArrayList, L> points)
	{
		return GeomMasks.realPointCollection(points);
	}

	/**
	 * Creates a {@link WritableRealPointCollection} containing the given points.
	 *
	 * @param points contains the locations of the points in this collection
	 * @return a {@link WritableRealPointCollection} containing the given points
	 */
	default <L extends RealLocalizable> WritableRealPointCollection<L>
		realPointCollection(final Collection<L> points)
	{
		return GeomMasks.realPointCollection(points);
	}

	/**
	 * Creates a read-only {@link RealPointCollection} containing the given
	 * points.
	 *
	 * @param tree a {@link KDTree} containing the location of each point in the
	 *          collection
	 * @return a {@link RealPointCollection} containing the given points
	 */
	default <L extends RealLocalizable> RealPointCollection<L>
		kDTreeRealPointCollection(final KDTree<L> tree)
	{
		return GeomMasks.kDTreeRealPointCollection(tree);
	}

	/**
	 * Creates a read-only {@link RealPointCollection} containing the given
	 * points.
	 *
	 * @param points contains the location of each point in the collection
	 * @return a {@link RealPointCollection} containing the given points
	 */
	default <L extends RealLocalizable> RealPointCollection<L>
		kDTreeRealPointCollection(final Collection<L> points)
	{
		return GeomMasks.kDTreeRealPointCollection(points);
	}

	/**
	 * Creates a {@link WritableRealPointCollection} containing the given points.
	 *
	 * @param points contains the locations of the points in the collection
	 * @return a {@link WritableRealPointCollection} containing the given points
	 */
	default <L extends RealLocalizable> WritableRealPointCollection<L>
		realPointSampleListRealPointCollection(
			final RealPointSampleList<L> points)
	{
		return GeomMasks.realPointSampleListRealPointCollection(points);
	}

	/**
	 * Creates a {@link WritableRealPointCollection} containing the given points.
	 *
	 * @param points contains the locations of the points in the collection
	 * @return a {@link WritableRealPointCollection} containing the given points
	 */
	default <L extends RealLocalizable> WritableRealPointCollection<L>
		realPointSampleListRealPointCollection(final Collection<L> points)
	{
		// TODO: Replace with GeoMasks when that's fixed
		return GeomMasks.realPointSampleListRealPointCollection(points);
	}

	// ---- Sphere ----

	/**
	 * Creates a {@link WritableSphere} with {@link BoundaryType#CLOSED closed}
	 * boundary behavior.
	 *
	 * @param center location of the center of the sphere
	 * @param radius radius of the sphere
	 * @return a sphere with the given radius positioned at the given center
	 */
	default WritableSphere closedSphere(final double[] center,
		final double radius)
	{
		return GeomMasks.closedSphere(center, radius);
	}

	/**
	 * Creates a {@link WritableSphere} with {@link BoundaryType#OPEN open}
	 * boundary behavior.
	 *
	 * @param center location of the center of the sphere
	 * @param radius radius of the sphere
	 * @return a sphere with the given radius positioned at the given center
	 */
	default WritableSphere openSphere(final double[] center,
		final double radius)
	{
		return GeomMasks.openSphere(center, radius);
	}

	// ---- SuperEllispoid ----

	/**
	 * Creates a {@link WritableSuperEllipsoid} with {@link BoundaryType#CLOSED
	 * closed} boundary behavior.
	 *
	 * @param center location of the center of the super-ellipsoid
	 * @param semiAxisLengths extension of the super-ellipsoid in each dimensions
	 * @param exponent exponent of the super-ellipsoid
	 * @return a super-ellipsoid with the given semi-axis lengths and exponent
	 *         positioned at the given center
	 */
	default WritableSuperEllipsoid closedSuperEllipsoid(final double[] center,
		final double[] semiAxisLengths, final double exponent)
	{
		return GeomMasks.closedSuperEllipsoid(center, semiAxisLengths,
			exponent);
	}

	/**
	 * Creates a {@link WritableSuperEllipsoid} with {@link BoundaryType#OPEN
	 * open} boundary behavior.
	 *
	 * @param center location of the center of the super-ellipsoid
	 * @param semiAxisLengths extension of the super-ellipsoid in each dimensions
	 * @param exponent exponent of the super-ellipsoid
	 * @return a super-ellipsoid with the given semi-axis lengths and exponent
	 *         positioned at the given center
	 */
	default WritableSuperEllipsoid openSuperEllipsoid(final double[] center,
		final double[] semiAxisLengths, final double exponent)
	{
		return GeomMasks.openSuperEllipsoid(center, semiAxisLengths,
			exponent);
	}
}
