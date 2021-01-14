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

import net.imglib2.type.numeric.complex.ComplexFloatType;

import org.scijava.AbstractContextual;

//TODO - uncomment when we are ready to support
//@Plugin(type = DataType.class)
/**
 * {@link DataType} definition for 64-bit complex float numbers.
 * 
 * @author Barry DeZonia
 */
public class DataType64BitSignedComplexFloat extends AbstractContextual
	implements DataType<ComplexFloatType>
{

	// -- fields --

	private ComplexFloatType type = new ComplexFloatType();

	// -- DataType methods --

	@Override
	public ComplexFloatType getType() {
		return type;
	}

	@Override
	public String shortName() {
		return "64-bit complex";
	}

	@Override
	public String longName() {
		return "64-bit complex float";
	}

	@Override
	public String description() {
		return "A complex floating data type with 32-bit float subcomponents";
	}

	@Override
	public boolean isComplex() {
		return true;
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
	public void lowerBound(ComplexFloatType dest) {
		throw new UnsupportedOperationException("complex numbers are unbounded");
	}

	@Override
	public void upperBound(ComplexFloatType dest) {
		throw new UnsupportedOperationException("complex numbers are unbounded");
	}

	@Override
	public int bitCount() {
		return 64;
	}

	@Override
	public ComplexFloatType createVariable() {
		return new ComplexFloatType();
	}

	@Override
	public void cast(ComplexFloatType val, BigComplex dest) {
		dest.setReal(val.getRealFloat());
		dest.setImag(val.getImaginaryFloat());
	}

	@Override
	public void cast(BigComplex val, ComplexFloatType dest) {
		dest.setReal(val.getReal().floatValue());
		dest.setImaginary(val.getImag().floatValue());
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
	public double asDouble(ComplexFloatType val) {
		throw new UnsupportedOperationException();
	}

	@Override
	public long asLong(ComplexFloatType val) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setDouble(ComplexFloatType val, double v) {
		val.setReal(v);
		val.setImaginary(0);
	}

	@Override
	public void setLong(ComplexFloatType val, long v) {
		val.setReal(v);
		val.setImaginary(0);
	}
}
