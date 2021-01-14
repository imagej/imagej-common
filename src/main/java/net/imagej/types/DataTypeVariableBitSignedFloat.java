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

package net.imagej.types;

import java.math.BigDecimal;

import org.scijava.AbstractContextual;

//TODO - uncomment when we are ready to support
//@Plugin(type = DataType.class)
/**
 * {@link DataType} definition for variable bit floats.
 * 
 * @author Barry DeZonia
 */
public class DataTypeVariableBitSignedFloat extends AbstractContextual implements
 DataType<PreciseFixedFloatType>
{

	private PreciseFixedFloatType type = new PreciseFixedFloatType();

	@Override
	public PreciseFixedFloatType getType() {
		return type;
	}

	@Override
	public String shortName() {
		return "Fixed float";
	}

	@Override
	public String longName() {
		return "Fixed point float";
	}

	@Override
	public String description() {
		return "A float data type whose size is unrestricted and precise to 25 decimal places";
	}

	@Override
	public boolean isComplex() {
		return false;
	}

	@Override
	public boolean isFloat() {
		return true;
	}

	@Override
	public boolean isSigned() {
		return true;
	}

	@Override
	public boolean isBounded() {
		return false;
	}

	@Override
	public void lowerBound(PreciseFixedFloatType dest) {
		throw new UnsupportedOperationException("This data type is unbounded");
	}

	@Override
	public void upperBound(PreciseFixedFloatType dest) {
		throw new UnsupportedOperationException("This data type is unbounded");
	}

	@Override
	public int bitCount() {
		return -1;
	}

	@Override
	public PreciseFixedFloatType createVariable() {
		return new PreciseFixedFloatType();
	}

	@Override
	public void cast(PreciseFixedFloatType val, BigComplex dest) {
		dest.setReal(val.get());
		dest.setImag(BigDecimal.ZERO);
	}

	@Override
	public void cast(BigComplex val, PreciseFixedFloatType dest) {
		dest.set(val.getReal());
	}

	@Override
	public boolean hasDoubleRepresentation() {
		return false;
	}

	@Override
	public boolean hasLongRepresentation() {
		return false;
	}

	@Override
	public double asDouble(PreciseFixedFloatType val) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long asLong(PreciseFixedFloatType val) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setDouble(PreciseFixedFloatType val, double v) {
		val.set(v);
	}

	@Override
	public void setLong(PreciseFixedFloatType val, long v) {
		val.set(v);
	}
}
