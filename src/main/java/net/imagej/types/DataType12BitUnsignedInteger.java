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

import net.imglib2.type.numeric.integer.Unsigned12BitType;

import org.scijava.AbstractContextual;
import org.scijava.plugin.Plugin;

/**
 * {@link DataType} definition for 12-bit unsigned integers.
 * 
 * @author Barry DeZonia
 */
@Plugin(type = DataType.class)
public class DataType12BitUnsignedInteger extends AbstractContextual implements
	DataType<Unsigned12BitType>
{

	private final Unsigned12BitType type = new Unsigned12BitType();

	@Override
	public Unsigned12BitType getType() {
		return type;
	}

	@Override
	public String shortName() {
		return "12-bit uint";
	}

	@Override
	public String longName() {
		return "12-bit unsigned integer";
	}

	@Override
	public String description() {
		return "An integer data type ranging between 0 and " + 0xfff;
	}

	@Override
	public boolean isComplex() {
		return false;
	}

	@Override
	public boolean isFloat() {
		return false;
	}

	@Override
	public boolean isSigned() {
		return false;
	}

	@Override
	public boolean isBounded() {
		return true;
	}

	@Override
	public void lowerBound(Unsigned12BitType dest) {
		dest.set((short) 0);
	}

	@Override
	public void upperBound(Unsigned12BitType dest) {
		dest.set((short) 0xfff);
	}

	@Override
	public int bitCount() {
		return 12;
	}

	@Override
	public Unsigned12BitType createVariable() {
		return new Unsigned12BitType();
	}

	@Override
	public void cast(Unsigned12BitType val, BigComplex dest) {
		dest.setReal(val.get());
		dest.setImag(BigDecimal.ZERO);
	}

	@Override
	public void cast(BigComplex val, Unsigned12BitType dest) {
		setLong(dest, val.getReal().longValue());
	}

	@Override
	public boolean hasDoubleRepresentation() {
		return true;
	}

	@Override
	public boolean hasLongRepresentation() {
		return true;
	}

	@Override
	public double asDouble(Unsigned12BitType val) {
		return val.get();
	}

	@Override
	public long asLong(Unsigned12BitType val) {
		return val.get();
	}

	@Override
	public void setDouble(Unsigned12BitType val, double v) {
		setLong(val, (long) v);
	}

	@Override
	public void setLong(Unsigned12BitType val, long v) {
		if (v < 0) val.set((short) 0);
		else if (v > 0xfff) val.set((short) 0xfff);
		else val.set((short) v);
	}
}
