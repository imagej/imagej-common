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

package net.imagej.interval;

import java.util.List;

import net.imagej.axis.AxisType;
import net.imagej.axis.CalibratedAxis;
import net.imglib2.Dimensions;
import net.imglib2.FinalDimensions;
import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.Positionable;
import net.imglib2.RealInterval;

/**
 * Abstract base class for {@link CalibratedInterval}.
 *
 * @author Mark Hiner <hinerm@gmail.com>
 */
public abstract class AbstractCalibratedInterval<A extends CalibratedAxis>
	extends AbstractCalibratedRealInterval<A>implements CalibratedInterval<A>
{

	private final Interval interval;

	// -- Constructors --

	public AbstractCalibratedInterval(final int n) {
		super(makeInterval(n));
		interval = new FinalInterval(n);
	}

	public AbstractCalibratedInterval(final int n, final A... axes) {
		super(makeInterval(n), axes);
		interval = new FinalInterval(n);
	}

	public AbstractCalibratedInterval(final int n, final List<A> axes) {
		super(makeInterval(n), axes);
		interval = new FinalInterval(n);
	}

	public AbstractCalibratedInterval(final Interval interval) {
		super(interval);
		this.interval = interval;
	}

	public AbstractCalibratedInterval(final Interval interval, final A... axes) {
		super(interval, axes);
		this.interval = interval;
	}

	public AbstractCalibratedInterval(final Interval interval,
		final List<A> axes)
	{
		super(interval, axes);
		this.interval = interval;
	}

	public AbstractCalibratedInterval(final Dimensions dimensions) {
		super(makeInterval(dimensions));
		interval = new FinalInterval(dimensions);
	}

	public AbstractCalibratedInterval(final Dimensions dimensions,
		final A... axes)
	{
		super(makeInterval(dimensions), axes);
		interval = new FinalInterval(dimensions);
	}

	public AbstractCalibratedInterval(final Dimensions dimensions,
		final List<A> axes)
	{
		super(makeInterval(dimensions), axes);
		interval = new FinalInterval(dimensions);
	}

	public AbstractCalibratedInterval(final long[] dimensions) {
		super(makeDouble(dimensions));
		interval = new FinalInterval(dimensions);

	}

	public AbstractCalibratedInterval(final long[] dimensions, final A... axes) {
		super(makeDouble(dimensions), axes);
		interval = new FinalInterval(dimensions);
	}

	public AbstractCalibratedInterval(final long[] dimensions,
		final List<A> axes)
	{
		super(makeDouble(dimensions), axes);
		interval = new FinalInterval(dimensions);
	}

	public AbstractCalibratedInterval(final long[] min, final long[] max) {
		super(makeDouble(min), makeDouble(max));
		interval = new FinalInterval(min, max);
	}

	public AbstractCalibratedInterval(final long[] min, final long[] max,
		final A... axes)
	{
		super(makeDouble(min), makeDouble(max), axes);
		interval = new FinalInterval(min, max);
	}

	public AbstractCalibratedInterval(final long[] min, final long[] max,
		final List<A> axes)
	{
		super(makeDouble(min), makeDouble(max), axes);
		interval = new FinalInterval(min, max);
	}

	// -- CalibratedInterval API --

	public long dimension(final AxisType axisType) {
		return dimension(dimensionIndex(axisType));
	}

	// -- Interval API --

	@Override
	public long min(final int d) {
		return interval.min(d);
	}

	@Override
	public void min(final long[] min) {
		interval.min(min);
	}

	@Override
	public void min(final Positionable min) {
		interval.min(min);
	}

	@Override
	public long max(final int d) {
		return interval.max(d);
	}

	@Override
	public void max(final long[] max) {
		interval.max(max);
	}

	@Override
	public void max(final Positionable max) {
		interval.max(max);
	}

	@Override
	public void dimensions(final long[] dimensions) {
		interval.dimensions(dimensions);
	}

	@Override
	public long dimension(final int d) {
		return interval.dimension(d);
	}

	// -- Helper methods --

	private static RealInterval makeInterval(final int n) {
		return makeInterval(new FinalDimensions(n));
	}

	private static RealInterval makeInterval(final Dimensions dimensions) {
		return new FinalInterval(dimensions);
	}

	private static double[] makeDouble(final long[] dimensions) {
		final double[] doubleDimensions = new double[dimensions.length];
		for (int i = 0; i < dimensions.length; i++) {
			doubleDimensions[i] = dimensions[i];
		}
		return doubleDimensions;
	}
}
