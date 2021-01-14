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
 * PowerAxis is a {@link CalibratedAxis } that scales raw values by the equation
 * y = a + b*x^c.
 * 
 * @author Barry DeZonia
 */
public class PowerAxis extends Variable3Axis {

	// -- constructors --

	public PowerAxis(final double power) {
		this(Axes.unknown(), power);
	}

	public PowerAxis(final AxisType type, final double power) {
		this(type, null, 0, 1, power);
	}

	public PowerAxis(final AxisType type, final String unit, final double a,
		final double b, final double c)
	{
		super(type, unit, a, b, c);
	}

	// -- CalibratedAxis methods --

	@Override
	public double calibratedValue(final double rawValue) {
		return a() + b() * Math.pow(rawValue, c());
	}

	@Override
	public double rawValue(final double calibratedValue) {
		return Math.pow(((calibratedValue - a()) / b()), (1.0 / c()));
	}

	@Override
	public String generalEquation() {
		return "y = a + b*x^c";
	}

	@Override
	public PowerAxis copy() {
		return new PowerAxis(type(), unit(), a(), b(), c());
	}

}
