/*
 * #%L
 * ImageJ2 software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2023 ImageJ2 developers.
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

import java.lang.reflect.Type;

import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealRandomAccessible;
import net.imglib2.RealRandomAccessibleRealInterval;
import net.imglib2.roi.Mask;
import net.imglib2.roi.MaskInterval;
import net.imglib2.roi.RealMask;
import net.imglib2.roi.RealMaskRealInterval;
import net.imglib2.roi.mask.integer.MaskAsRandomAccessible;
import net.imglib2.roi.mask.integer.MaskIntervalAsRandomAccessibleInterval;
import net.imglib2.roi.mask.integer.RandomAccessibleAsMask;
import net.imglib2.roi.mask.integer.RandomAccessibleIntervalAsMaskInterval;
import net.imglib2.roi.mask.real.RealMaskAsRealRandomAccessible;
import net.imglib2.roi.mask.real.RealMaskRealIntervalAsRealRandomAccessibleRealInterval;
import net.imglib2.roi.mask.real.RealRandomAccessibleAsRealMask;
import net.imglib2.roi.mask.real.RealRandomAccessibleRealIntervalAsRealMaskRealInterval;
import net.imglib2.type.logic.BoolType;

import org.scijava.Priority;
import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;

/**
 * Unwraps MaskPredicates which have been wrapped as (Real)RandomAccessibles,
 * and vice versa.
 *
 * @author Alison Walter
 */
public final class Unwrappers {

	private Unwrappers() {
		// NB: prevent instantiation of base class
	}

	/**
	 * Unwraps {@link MaskAsRandomAccessible}, provided it is of type
	 * {@link BoolType}.
	 */
	@Plugin(type = Converter.class, priority = Priority.HIGH)
	public static class MaskAsRandomAccessibleUnwrapper extends
		AbstractRAToMaskConverter<MaskAsRandomAccessible<BoolType>, Mask>
	{

		@Override
		public Class<Mask> getOutputType() {
			return Mask.class;
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Class<MaskAsRandomAccessible<BoolType>> getInputType() {
			return (Class) MaskAsRandomAccessible.class;
		}

		@Override
		public Mask convert(final MaskAsRandomAccessible<BoolType> src) {
			return src.getSource();
		}

	}

	/**
	 * Unwraps {@link MaskIntervalAsRandomAccessibleInterval}, provided it is of
	 * type {@link BoolType}.
	 */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public static class MaskIntervalAsRandomAccessibleIntervalUnwrapper extends
		AbstractRAToMaskConverter<MaskIntervalAsRandomAccessibleInterval<BoolType>, MaskInterval>
	{

		@Override
		public Class<MaskInterval> getOutputType() {
			return MaskInterval.class;
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Class<MaskIntervalAsRandomAccessibleInterval<BoolType>>
			getInputType()
		{
			return (Class) MaskIntervalAsRandomAccessibleInterval.class;
		}

		@Override
		public MaskInterval convert(
			final MaskIntervalAsRandomAccessibleInterval<BoolType> src)
		{
			return src.getSource();
		}

	}

	/**
	 * Unwraps {@link RandomAccessibleAsMask}, provided it is of type
	 * {@link BoolType}.
	 */
	@Plugin(type = Converter.class, priority = Priority.HIGH)
	public static class RandomAccessibleAsMaskUnwrapper extends
		AbstractMaskToRAConverter<RandomAccessibleAsMask<BoolType>, RandomAccessible<BoolType>>
	{

		@Override
		public boolean canConvert(final Object src, final Type dest) {
			return super.canConvert(src, dest) && MaskConversionUtil.isBoolType(
				((RandomAccessibleAsMask<?>) src).getSource());
		}

		@Override
		public boolean canConvert(final Object src, final Class<?> dest) {
			return super.canConvert(src, dest) && MaskConversionUtil.isBoolType(
				((RandomAccessibleAsMask<?>) src).getSource());
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Class<RandomAccessible<BoolType>> getOutputType() {
			return (Class) RandomAccessible.class;
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Class<RandomAccessibleAsMask<BoolType>> getInputType() {
			return (Class) RandomAccessibleAsMask.class;
		}

		@Override
		public RandomAccessible<BoolType> convert(
			final RandomAccessibleAsMask<BoolType> src)
		{
			return src.getSource();
		}

	}

	/**
	 * Unwraps {@link RandomAccessibleIntervalAsMaskInterval}, provided it is of
	 * type {@link BoolType}.
	 */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public static class RandomAccessibleIntervalAsMaskIntervalUnwrapper extends
		AbstractMaskToRAConverter<RandomAccessibleIntervalAsMaskInterval<BoolType>, RandomAccessibleInterval<BoolType>>
	{

		@Override
		public boolean canConvert(final Object src, final Type dest) {
			return super.canConvert(src, dest) && MaskConversionUtil.isBoolType(
				((RandomAccessibleIntervalAsMaskInterval<?>) src).getSource());
		}

		@Override
		public boolean canConvert(final Object src, final Class<?> dest) {
			return super.canConvert(src, dest) && MaskConversionUtil.isBoolType(
				((RandomAccessibleIntervalAsMaskInterval<?>) src).getSource());
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Class<RandomAccessibleInterval<BoolType>> getOutputType() {
			return (Class) RandomAccessibleInterval.class;
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Class<RandomAccessibleIntervalAsMaskInterval<BoolType>>
			getInputType()
		{
			return (Class) RandomAccessibleIntervalAsMaskInterval.class;
		}

		@Override
		public RandomAccessibleInterval<BoolType> convert(
			final RandomAccessibleIntervalAsMaskInterval<BoolType> src)
		{
			return src.getSource();
		}
	}

	/**
	 * Unwraps {@link RealMaskAsRealRandomAccessible}, provided it is of type
	 * {@link BoolType}.
	 */
	@Plugin(type = Converter.class, priority = Priority.HIGH)
	public static class RealMaskAsRealRandomAccessibleUnwrapper extends
		AbstractRRAToRealMaskConverter<RealMaskAsRealRandomAccessible<BoolType>, RealMask>
	{

		@Override
		public Class<RealMask> getOutputType() {
			return RealMask.class;
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Class<RealMaskAsRealRandomAccessible<BoolType>> getInputType() {
			return (Class) RealMaskAsRealRandomAccessible.class;
		}

		@Override
		public RealMask convert(
			final RealMaskAsRealRandomAccessible<BoolType> src)
		{
			return src.getSource();
		}

	}

	/**
	 * Unwraps {@link RealMaskRealIntervalAsRealRandomAccessibleRealInterval},
	 * provided it is of type {@link BoolType}.
	 */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public static class RealMaskRealIntervalAsRealRandomAccessibleRealIntervalUnwrapper
		extends
		AbstractRRAToRealMaskConverter<RealMaskRealIntervalAsRealRandomAccessibleRealInterval<BoolType>, RealMaskRealInterval>
	{

		@Override
		public Class<RealMaskRealInterval> getOutputType() {
			return RealMaskRealInterval.class;
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public
			Class<RealMaskRealIntervalAsRealRandomAccessibleRealInterval<BoolType>>
			getInputType()
		{
			return (Class) RealMaskRealIntervalAsRealRandomAccessibleRealInterval.class;
		}

		@Override
		public RealMaskRealInterval convert(
			final RealMaskRealIntervalAsRealRandomAccessibleRealInterval<BoolType> src)
		{
			return src.getSource();
		}

	}

	/**
	 * Unwraps {@link RealRandomAccessibleAsRealMask}, provided it is of type
	 * {@link BoolType}.
	 */
	@Plugin(type = Converter.class, priority = Priority.HIGH)
	public static class RealRandomAccessibleAsRealMaskUnwrapper extends
		AbstractRealMaskToRRAConverter<RealRandomAccessibleAsRealMask<BoolType>, RealRandomAccessible<BoolType>>
	{

		@Override
		public boolean canConvert(final Object src, final Type dest) {
			return super.canConvert(src, dest) && MaskConversionUtil.isBoolType(
				((RealRandomAccessibleAsRealMask<?>) src).getSource());
		}

		@Override
		public boolean canConvert(final Object src, final Class<?> dest) {
			return super.canConvert(src, dest) && MaskConversionUtil.isBoolType(
				((RandomAccessibleAsMask<?>) src).getSource());
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Class<RealRandomAccessible<BoolType>> getOutputType() {
			return (Class) RealRandomAccessible.class;
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Class<RealRandomAccessibleAsRealMask<BoolType>> getInputType() {
			return (Class) RealRandomAccessibleAsRealMask.class;
		}

		@Override
		public RealRandomAccessible<BoolType> convert(
			final RealRandomAccessibleAsRealMask<BoolType> src)
		{
			return src.getSource();
		}

	}

	/**
	 * Unwraps {@link RealRandomAccessibleRealIntervalAsRealMaskRealInterval},
	 * provided it is of type {@link BoolType}.
	 */
	@Plugin(type = Converter.class, priority = Priority.VERY_HIGH)
	public static class RealRandomAccessibleRealIntervalAsRealMaskRealIntervalUnwrapper
		extends
		AbstractRealMaskToRRAConverter<RealRandomAccessibleRealIntervalAsRealMaskRealInterval<BoolType>, RealRandomAccessibleRealInterval<BoolType>>
	{

		@Override
		public boolean canConvert(final Object src, final Type dest) {
			return super.canConvert(src, dest) && MaskConversionUtil.isBoolType(
				((RealRandomAccessibleRealIntervalAsRealMaskRealInterval<?>) src)
					.getSource());
		}

		@Override
		public boolean canConvert(final Object src, final Class<?> dest) {
			return super.canConvert(src, dest) && MaskConversionUtil.isBoolType(
				((RealRandomAccessibleRealIntervalAsRealMaskRealInterval<?>) src)
					.getSource());
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Class<RealRandomAccessibleRealInterval<BoolType>> getOutputType() {
			return (Class) RealRandomAccessibleRealInterval.class;
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public
			Class<RealRandomAccessibleRealIntervalAsRealMaskRealInterval<BoolType>>
			getInputType()
		{
			return (Class) RealRandomAccessibleRealIntervalAsRealMaskRealInterval.class;
		}

		@Override
		public RealRandomAccessibleRealInterval<BoolType> convert(
			final RealRandomAccessibleRealIntervalAsRealMaskRealInterval<BoolType> src)
		{
			return src.getSource();
		}

	}

}
