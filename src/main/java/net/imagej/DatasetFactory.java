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

package net.imagej;

import java.util.function.Function;
import java.util.function.Supplier;

import net.imglib2.Dimensions;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

/**
 * A factory which produces {@link Dataset}s.
 * <p>
 * This class extends the more type-safe {@link DatasetImgFactory} to eliminate
 * the generic parameter.
 * </p>
 *
 * @author Curtis Rueden
 */
public class DatasetFactory extends DatasetImgFactory<RealType<?>> {

	private Function<Img<?>, Dataset> wrapper;

	public DatasetFactory(final RealType<?> type, final Dataset dataset,
		final ImgCreator creator, final Function<Img<?>, Dataset> wrapper)
	{
		super(type, dataset, creator);
		this.wrapper = wrapper;
	}

	@Override
	public Dataset create(final long... dimensions) {
		return wrapper.apply(super.create(dimensions));
	}

	@Override
	public Dataset create(final Dimensions dimensions) {
		// NB: super.create(Dimensions) calls create(long[]).
		return (Dataset) super.create(dimensions);
	}

	@Override
	public Dataset create(final int[] dimensions) {
		// NB: super.create(int[]) calls create(long[]).
		return (Dataset) super.create(dimensions);
	}

	@Deprecated
	@Override
	public Dataset create(final long[] dim, final RealType<?> type) {
		return wrapper.apply(super.create(dim, type));
	}

	@Deprecated
	@Override
	public Dataset create(final Dimensions dim, final RealType<?> type) {
		// NB: super.create(Dimensions, RealType) calls create(long[], RealType).
		return (Dataset) super.create(dim, type);
	}

	@Deprecated
	@Override
	public Dataset create(final int[] dim, final RealType<?> type) {
		// NB: super.create(int[], RealType) calls create(long[], RealType).
		return (Dataset) super.create(dim, type);
	}

	@Deprecated
	@Override
	public Dataset create(final Supplier<RealType<?>> typeSupplier,
		final long... dim)
	{
		// NB: super.create(Supplier, long[]) calls create(long[], RealType).
		return (Dataset) super.create(typeSupplier, dim);
	}

	@Deprecated
	@Override
	public Dataset create(final Supplier<RealType<?>> typeSupplier,
		final Dimensions dim)
	{
		// NB: super.create(Supplier, Dimensions) calls create(Dimensions, RealType).
		return (Dataset) super.create(typeSupplier, dim);
	}

	@Deprecated
	@Override
	public Dataset create(final Supplier<RealType<?>> typeSupplier,
		final int[] dim)
	{
		// NB: super.create(Supplier, int[]) calls create(int[], RealType).
		return (Dataset) super.create(typeSupplier, dim);
	}
}
