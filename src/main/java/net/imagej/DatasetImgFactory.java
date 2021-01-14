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

import net.imglib2.Dimensions;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.util.Util;

/**
 * Base class for {@link ImgFactory} objects of {@link Dataset} implementations.
 *
 * @author Curtis Rueden
 */
public class DatasetImgFactory<T> extends ImgFactory<T> {

	private Dataset dataset;
	private ImgCreator creator;

	public DatasetImgFactory(final T type, final Dataset dataset,
		final ImgCreator creator)
	{
		super(type);
		this.dataset = dataset;
		this.creator = creator;
	}

	@Override
	public Img<T> create(final long... dimensions) {
		return creator.create(dataset, dimensions, type());
	}

	@Override
	public Img<T> create(final Dimensions dimensions) {
		final long[] size = new long[dimensions.numDimensions()];
		dimensions.dimensions(size);
		return create(size);
	}

	@Override
	public Img<T> create(final int[] dimensions) {
		return create(Util.int2long(dimensions));
	}

	@Override
	public <S> DatasetImgFactory<S> imgFactory(final S type)
		throws IncompatibleTypeException
	{
		return new DatasetImgFactory<>(type, dataset, creator);
	}

	@Deprecated
	@Override
	public Img<T> create(final long[] dim, final T type) {
		return creator.create(dataset, dim, type);
	}

	interface ImgCreator {
		<T> Img<T> create(Dataset dataset, long[] dims, T type);
	}
}
