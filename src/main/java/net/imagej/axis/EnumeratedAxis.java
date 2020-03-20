package net.imagej.axis;

import java.util.Arrays;

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

	public EnumeratedAxis(final AxisType type, final double[] values) {
		super(type);
		setValues(values);
	}

	public EnumeratedAxis(final AxisType type, final String unit,
		final double[] values)
	{
		super(type, unit);
		setValues(values);
	}

	// -- EnumeratedAxis methods --

	public void setValues(final double[] values) {
		if (values == null || values.length < 1) {
			throw new IllegalArgumentException("Need at least one value");
		}
		this.values = values;
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
		if (rawValue >= Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Raw value too large: " + rawValue);
		}
		final int i0 = (int) Math.floor(rawValue);
		final int i1 = (int) Math.ceil(rawValue);
		if (i0 == i1 && i0 >= 0 && i0 < values.length) {
			// Integer index with known calibrated value: return it directly.
			return values[i0];
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
		return (1 - w) * values[i0] + w * values[i1];
	}

	@Override
	public double rawValue(final double calibratedValue) {
		// NB: Assumes values array is sorted! We should document and/or enforce this.
		final int index = Arrays.binarySearch(values, calibratedValue);
		// TODO finish edge cases here.
		return index;
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
