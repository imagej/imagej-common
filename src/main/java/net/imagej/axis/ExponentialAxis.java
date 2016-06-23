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

package net.imagej.axis;


/**
 * ExponentialAxis is a {@link CalibratedAxis } that scales raw values by the
 * equation {@code y = a + b * exp(c + d*x)}.
 * 
 * @author Barry DeZonia
 */
public class ExponentialAxis extends Variable4Axis {

	// -- constructors --

	public ExponentialAxis() {
		this(Axes.unknown(), null, 0, 1, 0, 1);
	}

	public ExponentialAxis(final AxisType type, final String unit,
		final double a, final double b, final double c, final double d)
	{
		super(type, unit, a, b, c, d);
	}

	// -- CalibratedAxis methods --

	@Override
	public double calibratedValue(final double rawValue) {
		return a() + b() * Math.exp(c() + d() * rawValue);
	}

	@Override
	public double rawValue(final double calibratedValue) {
		return (Math.log((calibratedValue - a()) / b()) - c()) / d();
	}

	@Override
	public String generalEquation() {
		return "y = a + b * exp(c + d*x)";
	}

	@Override
	public ExponentialAxis copy() {
		return new ExponentialAxis(type(), unit(), a(), b(), c(), d());
	}

}
