/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2016 Board of Regents of the University of
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

import net.imagej.axis.CalibratedAxis;
import net.imagej.axis.IdentityAxis;
import net.imglib2.RealInterval;

/**
 * Default implementation of {@link CalibratedRealInterval}.
 * 
 * @author Barry DeZonia
 */
public final class DefaultCalibratedRealInterval extends
	AbstractCalibratedRealInterval<CalibratedAxis>
{

	// -- public constructors --

	public DefaultCalibratedRealInterval(final RealInterval interval) {
		super(interval);
		assignDefaultAxes(interval.numDimensions());
	}

	public DefaultCalibratedRealInterval(final RealInterval interval,
		final CalibratedAxis... axes)
	{
		super(interval, axes);
	}

	public DefaultCalibratedRealInterval(final RealInterval interval,
		final List<CalibratedAxis> axes)
	{
		super(interval, axes);
	}

	public DefaultCalibratedRealInterval(final double[] extents) {
		super(extents);
		assignDefaultAxes(extents.length);
	}

	public DefaultCalibratedRealInterval(final double[] extents,
		final CalibratedAxis... axes)
	{
		super(extents, axes);
	}

	public DefaultCalibratedRealInterval(final double[] extents,
		final List<CalibratedAxis> axes)
	{
		super(extents, axes);
	}

	public DefaultCalibratedRealInterval(final double[] min, final double[] max) {
		super(min, max);
		assignDefaultAxes(min.length);
	}

	public DefaultCalibratedRealInterval(final double[] min, final double[] max,
		final CalibratedAxis... axes)
	{
		super(min, max, axes);
	}

	public DefaultCalibratedRealInterval(final double[] min, final double[] max,
		final List<CalibratedAxis> axes)
	{
		super(min, max, axes);
	}

	// -- Helper methods --

	private void assignDefaultAxes(final int numDimensions) {
		for (int d = 0; d < numDimensions; d++) {
			setAxis(new IdentityAxis(), d);
		}
	}

}
