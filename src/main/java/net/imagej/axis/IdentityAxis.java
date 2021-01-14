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
 * IdentityAxis is a {@link CalibratedAxis} whose raw and calibrated values are
 * the same.
 * 
 * @author Barry DeZonia
 */
public class IdentityAxis extends VariableAxis {

	// -- constructors --

	/** Constructs a default IdentityAxis of unknown axis type. */
	public IdentityAxis() {
		this(Axes.unknown());
	}

	/** Constructs an IdentityAxis of the specified axis type. */
	public IdentityAxis(final AxisType type) {
		super(type);
	}

	/** Constructs an IdentityAxis of the specified axis type and unit. */
	public IdentityAxis(final AxisType type, final String unit) {
		super(type, unit);
	}

	// -- CalibratedAxis methods --

	@Override
	public double calibratedValue(final double rawValue) {
		return rawValue;
	}

	@Override
	public double rawValue(final double calibratedValue) {
		return calibratedValue;
	}

	@Override
	public String generalEquation() {
		return "y = x";
	}

	@Override
	public IdentityAxis copy() {
		return new IdentityAxis(type(), unit());
	}

}
