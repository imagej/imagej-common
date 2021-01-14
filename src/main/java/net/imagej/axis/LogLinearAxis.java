/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2021 ImageJ developers.
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
 * LogLinearAxis is a {@link CalibratedAxis } that scales raw values by the
 * equation {@code y = a + b * ln(c + d*x)}.
 * 
 * @author Barry DeZonia
 */
public class LogLinearAxis extends Variable4Axis {

	// -- constructors --

	public LogLinearAxis() {
		this(Axes.unknown());
	}

	public LogLinearAxis(final AxisType type) {
		this(type, null);
	}

	public LogLinearAxis(final AxisType type, final String unit) {
		this(type, unit, 0, 1, 1, 1);
	}

	public LogLinearAxis(final AxisType type, final String unit, final double a,
		final double b, final double c, final double d)
	{
		super(type, unit, a, b, c, d);
	}

	// -- CalibratedAxis methods --

	@Override
	public double calibratedValue(final double rawValue) {
		return a() + b() * Math.log(c() + d() * rawValue);
	}

	@Override
	public double rawValue(final double calibratedValue) {
		return ((Math.exp((calibratedValue - a()) / b())) - c()) / d();
	}

	@Override
	public String generalEquation() {
		return "y = a + b * ln(c + d * x)";
	}

	@Override
	public LogLinearAxis copy() {
		return new LogLinearAxis(type(), unit(), a(), b(), c(), d());
	}

}
