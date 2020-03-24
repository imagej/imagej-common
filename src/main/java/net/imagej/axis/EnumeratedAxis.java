/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2020 Board of Regents of the University of
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

import java.util.Arrays;
import java.util.List;

/**
 * A {@link CalibratedAxis} whose coordinate values are explicitly enumerated by
 * a list. Out-of-bounds calibrated values are linearly extrapolated from the
 * two nearest known coordinates. Non-integer calibrated values are linearly
 * interpolated from the two nearest integer indices.
 * 
 * @author Curtis Rueden
 */
public class EnumeratedAxis extends AbstractCalibratedAxis {

	private double[] values;
	private boolean invertible;

	/**
	 * Creates an axis whose calibrated values are defined by the given list of
	 * {@code values}. This is a convenience constructor which copies the list
	 * values to a new array internally.
	 * 
	 * @param type The type of axis (e.g. {@link Axes#X} or {@link Axes#TIME}.
	 * @param values List of calibrated values. The first element of the list is
	 *          the calibrated value at raw position 0, the second element is the
	 *          calibrated value at raw position 1, and so forth. Intermediate and
	 *          out-of-bounds calibrated values are inferred by linear
	 *          interpolation or extrapolation, respectively.
	 */
	public EnumeratedAxis(final AxisType type, final List<? extends Number> values) {
		this(type, values.stream().mapToDouble(x -> x.doubleValue()).toArray());
	}

	/**
	 * Creates an axis whose calibrated values are defined by the given
	 * {@code values} array.
	 * 
	 * @param type The type of axis (e.g. {@link Axes#X} or {@link Axes#TIME}.
	 * @param values Array of calibrated values. The first element of the array is
	 *          the calibrated value at raw position 0, the second element is the
	 *          calibrated value at raw position 1, and so forth. Intermediate and
	 *          out-of-bounds calibrated values are inferred by linear
	 *          interpolation or extrapolation, respectively.
	 */
	public EnumeratedAxis(final AxisType type, final double[] values) {
		super(type);
		setValues(values);
	}

	/**
	 * Creates an axis whose calibrated values are defined by the given
	 * {@code values} array.
	 * 
	 * @param type The type of axis (e.g. {@link Axes#X} or {@link Axes#TIME}.
	 * @param unit The unit of the axis (e.g. microns or seconds).
	 * @param values Array of calibrated values. The first element of the array is
	 *          the calibrated value at raw position 0, the second element is the
	 *          calibrated value at raw position 1, and so forth. Intermediate and
	 *          out-of-bounds calibrated values are inferred by linear
	 *          interpolation or extrapolation, respectively.
	 */
	public EnumeratedAxis(final AxisType type, final String unit,
		final double[] values)
	{
		super(type, unit);
		setValues(values);
	}

	// -- EnumeratedAxis methods --

	/**
	 * Sets the axis's calibrated values. The array reference is used directly,
	 * without copying.
	 * 
	 * @param values Array of calibrated values. The first element of the array is
	 *          the calibrated value at raw position 0, the second element is the
	 *          calibrated value at raw position 1, and so forth. Intermediate and
	 *          out-of-bounds calibrated values are inferred by linear
	 *          interpolation or extrapolation, respectively.
	 */
	public void setValues(final double[] values) {
		if (values == null || values.length < 1) {
			throw new IllegalArgumentException("Need at least one value");
		}
		this.values = values;

		// Check for monotonically increasing values, for invertibility.
		// (While monotonically decreasing values would also be invertible,
		// this implementation doesn't currently handle it.)
		invertible = true;
		for (int i = 0; i < values.length - 1; i++) {
			if (values[i] >= values[i+1]) {
				invertible = false;
				break;
			}
		}
	}

	// -- Object methods --

	@Override
	public int hashCode() {
		return super.hashCode() ^ Arrays.hashCode(values);
	}

	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof EnumeratedAxis)) return false;
		if (!super.equals(o)) return false;
		final EnumeratedAxis other = (EnumeratedAxis) o;
		return Arrays.equals(values, other.values);
	}

	// -- CalibratedAxis methods --

	@Override
	public double calibratedValue(final double rawValue) {
		if (values.length == 1) return values[0]; // Constant-valued axis.
		final double i0 = Math.floor(rawValue);
		final double i1 = Math.ceil(rawValue);
		if (i0 == i1 && i0 >= 0 && i0 < values.length) {
			// Integer index with known calibrated value: return it directly.
			return values[(int) i0];
		}
		if (i0 < 0) {
			// Extrapolate from first two values.
			final double slope = values[1] - values[0];
			final double offset = values[0];
			return slope * rawValue + offset;
		}
		if (i1 >= values.length) {
			// Extrapolate from last two values.
			final int len = values.length;
			final double slope = values[len - 1] - values[len - 2];
			final double offset = values[len - 1];
			return slope * (rawValue - len + 1) + offset;
		}
		// Interpolate between two nearest values.
		final double w = rawValue - i0;
		return (1 - w) * values[(int) i0] + w * values[(int) i1];
	}

	@Override
	public double rawValue(final double calibratedValue) {
		if (!invertible) {
			throw new UnsupportedOperationException(
				"Non-invertible calibration values");
		}

		if (values.length == 1) return 0; // Constant-valued axis.

		// Binary search for the nearest calibrated values.
		final int index = Arrays.binarySearch(values, calibratedValue);
		if (index >= 0) {
			// Integer index with known calibrated value: return index directly.
			return index;
		}
		final int i0 = -index - 2;
		final int i1 = -index - 1;
		if (i0 < 0) {
			// Extrapolate from first two values.
			final double slope = values[1] - values[0];
			final double offset = values[0];
			return (calibratedValue - offset) / slope;
		}
		if (i1 >= values.length) {
			// Extrapolate from last two values.
			final int len = values.length;
			final double slope = values[len - 1] - values[len - 2];
			final double offset = values[len - 1];
			return (calibratedValue - offset) / slope + len - 1;
		}
		// Interpolate between two nearest values.
		final double frac = (values[i1] - calibratedValue) / (values[i1] - values[i0]);
		return i1 - frac;
	}

	@Override
	public String generalEquation() {
		return "N/A";
	}

	@Override
	public String particularEquation() {
		return "N/A";
	}

	@Override
	public EnumeratedAxis copy() {
		return new EnumeratedAxis(type(), unit(), values.clone());
	}
}
