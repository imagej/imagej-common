/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2020 ImageJ developers.
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

package net.imagej.roi;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.roi.MaskPredicate;
import net.imglib2.type.logic.BoolType;
import net.imglib2.util.Util;

import org.scijava.util.GenericUtils;

/**
 * Utility class for working with {@link MaskPredicate}s.
 *
 * @author Alison Walter
 */
final public class MaskConversionUtil {

	private MaskConversionUtil() {}

	/**
	 * Gets parameterized {@link Type} for
	 * {@code RealRandomAccessibleRealInterval<BoolType>}.
	 */
	public static Type realRandomAccessibleRealIntervalType() {
		return GenericUtils.getMethodReturnType(method(
			"realRandomAccessibleRealInterval"), MaskConversionUtil.class);
	}

	/**
	 * Gets parameterized {@link Type} for {@code RealRandomAccessible<BoolType>}.
	 */
	public static Type realRandomAccessibleType() {
		return GenericUtils.getMethodReturnType(method("realRandomAccessible"),
			MaskConversionUtil.class);
	}

	/**
	 * Gets parameterized {@link Type} for
	 * {@code RandomAccessibleInterval<BoolType>}.
	 */
	public static Type randomAccessibleIntervalType() {
		return GenericUtils.getMethodReturnType(method("randomAccessibleInterval"),
			MaskConversionUtil.class);
	}

	/** Gets parameterized {@link Type} for {@code RandomAccessible<BoolType>}. */
	public static Type randomAccessibleType() {
		return GenericUtils.getMethodReturnType(method("randomAccessible"),
			MaskConversionUtil.class);
	}

	public static boolean isBoolType(final RandomAccessible<?> ra) {
		if (ra instanceof Interval) return Util.getTypeFromInterval(
			(RandomAccessibleInterval<?>) ra) instanceof BoolType;
		return ra.randomAccess().get() instanceof BoolType;
	}

	public static boolean isBoolType(final RealRandomAccessible<?> ra) {
		if (ra instanceof RealInterval) return Util.getTypeFromRealInterval(
			(RealRandomAccessibleRealInterval<?>) ra) instanceof BoolType;
		return ra.realRandomAccess().get() instanceof BoolType;
	}

	public static boolean isBoolType(final Type type) {
		if (!(type instanceof ParameterizedType)) return false;
		final Type[] types = ((ParameterizedType) type).getActualTypeArguments();
		return types.length >= 1 && types[0].equals(BoolType.class);
	}

	// -- Helper methods --

	private static Method method(final String name) {
		try {
			return MaskConversionUtil.class.getDeclaredMethod(name);
		}
		catch (NoSuchMethodException | SecurityException exc) {
			return null;
		}
	}

	@SuppressWarnings("unused")
	private static RealRandomAccessibleRealInterval<BoolType>
		realRandomAccessibleRealInterval()
	{
		return null;
	}

	@SuppressWarnings("unused")
	private static RealRandomAccessible<BoolType> realRandomAccessible() {
		return null;
	}

	@SuppressWarnings("unused")
	private static RandomAccessibleInterval<BoolType> randomAccessibleInterval() {
		return null;
	}

	@SuppressWarnings("unused")
	private static RandomAccessible<BoolType> randomAccessible() {
		return null;
	}
}
