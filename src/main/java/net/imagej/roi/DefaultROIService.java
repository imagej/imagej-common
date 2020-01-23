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

package net.imagej.roi;

import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.roi.Mask;
import net.imglib2.roi.MaskInterval;
import net.imglib2.roi.MaskPredicate;
import net.imglib2.roi.RealMask;
import net.imglib2.roi.RealMaskRealInterval;
import net.imglib2.type.logic.BoolType;

import org.scijava.convert.ConvertService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

/**
 * Default implementation of {@link ROIService}.
 *
 * @author Alison Walter
 */
@Plugin(type = Service.class)
public class DefaultROIService extends AbstractService implements ROIService {

	@Parameter
	private ConvertService convertService;

	@Override
	public Mask toMask(final Object o) {
		final String returnType = "Mask";
		checkNull(o, returnType);

		// If o is already a Mask, the CastingConverter should be called and no
		// actual conversion will occur
		final Mask m = convertService.convert(o, net.imglib2.roi.Mask.class);
		if (m != null) return m;

		final RandomAccessible<?> ra = (RandomAccessible<?>) convertService.convert(
			o, MaskConversionUtil.randomAccessibleType());
		if (ra != null && MaskConversionUtil.isBoolType(ra)) return toMask(ra);

		throw cannotConvert(o, returnType);
	}

	@Override
	public MaskInterval toMaskInterval(final Object o) {
		final String returnType = "MaskInterval";
		checkNull(o, returnType);

		// If o is already a Mask, the CastingConverter should be called and no
		// actual conversion will occur
		final MaskInterval mi = convertService.convert(o, MaskInterval.class);
		if (mi != null) return mi;

		final RandomAccessibleInterval<?> rai =
			(RandomAccessibleInterval<?>) convertService.convert(o, MaskConversionUtil
				.randomAccessibleIntervalType());
		if (rai != null && MaskConversionUtil.isBoolType(rai)) return toMaskInterval(
			rai);

		throw cannotConvert(o, returnType);
	}

	@Override
	public RealMask toRealMask(final Object o) {
		final String returnType = "RealMask";
		checkNull(o, returnType);

		// If o is already a Mask, the CastingConverter should be called and no
		// actual conversion will occur
		final RealMask m = convertService.convert(o, RealMask.class);
		if (m != null) return m;

		final RealRandomAccessible<?> rra = (RealRandomAccessible<?>) convertService
			.convert(o, MaskConversionUtil.realRandomAccessibleType());
		if (rra != null && MaskConversionUtil.isBoolType(rra)) return toRealMask(
			rra);

		throw cannotConvert(o, returnType);
	}

	@Override
	public RealMaskRealInterval toRealMaskRealInterval(final Object o) {
		final String returnType = "RealMaskRealInterval";
		checkNull(o, returnType);

		// If o is already a Mask, the CastingConverter should be called and no
		// actual conversion will occur
		final RealMaskRealInterval mri = convertService.convert(o,
			RealMaskRealInterval.class);
		if (mri != null) return mri;

		final RealRandomAccessibleRealInterval<?> rrari =
			(RealRandomAccessibleRealInterval<?>) convertService.convert(o,
				MaskConversionUtil.realRandomAccessibleRealIntervalType());
		if (rrari != null && MaskConversionUtil.isBoolType(rrari))
			return toRealMaskRealInterval(rrari);

		throw cannotConvert(o, returnType);
	}

	@Override
	public MaskPredicate<?> toMaskPredicate(final Object o) {
		final String returnType = "MaskPredicate";
		checkNull(o, returnType);

		// If o is already a Mask, the CastingConverter should be called and no
		// actual conversion will occur
		final Mask m = convertService.convert(o, Mask.class);
		if (m != null) return m;

		// RealMask
		final RealMask rm = convertService.convert(o, RealMask.class);
		if (rm != null) return rm;

		final RandomAccessible<?> ra = (RandomAccessible<?>) convertService.convert(
			o, MaskConversionUtil.randomAccessibleType());
		if (ra != null && MaskConversionUtil.isBoolType(ra)) return toMask(ra);

		final RealRandomAccessible<?> rra = (RealRandomAccessible<?>) convertService
			.convert(o, MaskConversionUtil.realRandomAccessibleType());
		if (rra != null && MaskConversionUtil.isBoolType(rra)) return toRealMask(
			rra);

		throw cannotConvert(o, returnType);
	}

	@Override
	@SuppressWarnings("unchecked")
	public RandomAccessible<BoolType> toRandomAcessible(final Object o) {
		final String returnType = "RandomAccessible<BoolType>";
		checkNull(o, returnType);

		final RandomAccessible<?> ra = (RandomAccessible<?>) convertService.convert(
			o, MaskConversionUtil.randomAccessibleType());
		if (ra != null && MaskConversionUtil.isBoolType(ra))
			return (RandomAccessible<BoolType>) ra;

		final Mask m = convertService.convert(o, Mask.class);
		if (m != null) return toRandomAcessible(m);

		throw cannotConvert(o, returnType);
	}

	@Override
	@SuppressWarnings("unchecked")
	public RandomAccessibleInterval<BoolType> toRandomAccessibleInterval(
		final Object o)
	{
		final String returnType = "RandomAccessibleInterval<BoolType>";
		checkNull(o, returnType);

		final RandomAccessibleInterval<?> rai =
			(RandomAccessibleInterval<?>) convertService.convert(o, MaskConversionUtil
				.randomAccessibleIntervalType());
		if (rai != null && MaskConversionUtil.isBoolType(rai))
			return (RandomAccessibleInterval<BoolType>) rai;

		final MaskInterval mi = convertService.convert(o, MaskInterval.class);
		if (mi != null) return toRandomAccessibleInterval(mi);

		throw cannotConvert(o, returnType);
	}

	@Override
	@SuppressWarnings("unchecked")
	public RealRandomAccessible<BoolType> toRealRandomAccessible(final Object o) {
		final String returnType = "RealRandomAccessible<BoolType>";
		checkNull(o, returnType);

		final RealRandomAccessible<?> rra = (RealRandomAccessible<?>) convertService
			.convert(o, MaskConversionUtil.realRandomAccessibleType());
		if (rra != null && MaskConversionUtil.isBoolType(rra))
			return (RealRandomAccessible<BoolType>) rra;

		final RealMask m = convertService.convert(o, RealMask.class);
		if (m != null) return toRealRandomAccessible(m);

		throw cannotConvert(o, returnType);
	}

	@Override
	@SuppressWarnings("unchecked")
	public RealRandomAccessibleRealInterval<BoolType>
		toRealRandomAccessibleRealInterval(final Object o)
	{
		final String returnType = "RealRandomAccessibleRealInterval<BoolType>";
		checkNull(o, returnType);

		final RealRandomAccessibleRealInterval<?> rrari =
			(RealRandomAccessibleRealInterval<?>) convertService.convert(o,
				MaskConversionUtil.realRandomAccessibleRealIntervalType());
		if (rrari != null && MaskConversionUtil.isBoolType(rrari))
			return (RealRandomAccessibleRealInterval<BoolType>) rrari;

		final RealMaskRealInterval mri = convertService.convert(o,
			RealMaskRealInterval.class);
		if (mri != null) return toRealRandomAccessibleRealInterval(mri);

		throw cannotConvert(o, returnType);
	}

	// -- Helper methods --

	private void checkNull(final Object o, final String s) {
		if (o == null) throw new IllegalArgumentException(
			"Cannot convert null to " + s);
	}

	private IllegalArgumentException cannotConvert(final Object o,
		final String s)
	{
		return new IllegalArgumentException("Cannot convert " + o.getClass() +
			" to " + s);
	}
}
