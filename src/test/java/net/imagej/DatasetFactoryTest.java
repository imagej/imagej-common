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

package net.imagej;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.function.Function;

import net.imglib2.Dimensions;
import net.imglib2.FinalDimensions;
import net.imglib2.IterableInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Test;

/**
 * Tests {@link DatasetFactory}.
 *
 * @author Curtis Rueden
 */
public class DatasetFactoryTest extends AbstractDatasetTest {

	final int[] iDims = { 12, 34 };
	final long[] lDims = { 12, 34 };
	final Dimensions dDims = FinalDimensions.wrap(lDims);

	@Test
	public void testCreateLongArray() {
		assertCreatedOK(createPlanarDataset(), d -> d.factory().create(lDims), dDims);
		assertCreatedOK(createNonplanarDataset(), d -> d.factory().create(lDims), dDims);
	}

	@Test
	public void testCreateDimensions() {
		assertCreatedOK(createPlanarDataset(), d -> d.factory().create(dDims), dDims);
		assertCreatedOK(createNonplanarDataset(), d -> d.factory().create(dDims), dDims);
	}

	@Test
	public void testCreateIntArray() {
		assertCreatedOK(createPlanarDataset(), d -> d.factory().create(iDims), dDims);
		assertCreatedOK(createNonplanarDataset(), d -> d.factory().create(iDims), dDims);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testCreateLongArrayAndType() {
		assertCreatedOK(createPlanarDataset(), d -> d.factory().create(lDims, new DoubleType()), dDims, DoubleType.class);
		assertCreatedOK(createNonplanarDataset(), d -> d.factory().create(lDims, new DoubleType()), dDims, DoubleType.class);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testCreateDimensionsAndType() {
		assertCreatedOK(createPlanarDataset(), d -> d.factory().create(dDims, new DoubleType()), dDims, DoubleType.class);
		assertCreatedOK(createNonplanarDataset(), d -> d.factory().create(dDims, new DoubleType()), dDims, DoubleType.class);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testCreateIntArrayAndType() {
		assertCreatedOK(createPlanarDataset(), d -> d.factory().create(iDims, new DoubleType()), dDims, DoubleType.class);
		assertCreatedOK(createNonplanarDataset(), d -> d.factory().create(iDims, new DoubleType()), dDims, DoubleType.class);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testCreateSupplierAndLongArray() {
		assertCreatedOK(createPlanarDataset(), d -> d.factory().create(() -> new DoubleType(), lDims), dDims, DoubleType.class);
		assertCreatedOK(createNonplanarDataset(), d -> d.factory().create(() -> new DoubleType(), lDims), dDims, DoubleType.class);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testCreateSupplierAndDimensions() {
		assertCreatedOK(createPlanarDataset(), d -> d.factory().create(() -> new DoubleType(), dDims), dDims, DoubleType.class);
		assertCreatedOK(createNonplanarDataset(), d -> d.factory().create(() -> new DoubleType(), dDims), dDims, DoubleType.class);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testCreateSupplierAndIntArray() {
		assertCreatedOK(createPlanarDataset(), d -> d.factory().create(() -> new DoubleType(), iDims), dDims, DoubleType.class);
		assertCreatedOK(createNonplanarDataset(), d -> d.factory().create(() -> new DoubleType(), iDims), dDims, DoubleType.class);
	}

	@Test
	public void testImgFactory() {
		assertCreatedOK(createPlanarDataset(), d -> d.factory().imgFactory(new DoubleType()).create(d), DoubleType.class);
		assertCreatedOK(createNonplanarDataset(), d -> d.factory().imgFactory(new DoubleType()).create(d), DoubleType.class);
	}

	// -- Helper methods --

	private void assertCreatedOK(final Dataset dataset,
		final Function<Dataset, ? extends Img<?>> creator, final Class<?> eType)
	{
		assertCreatedOK(dataset, creator, dataset, eType);
	}

	private void assertCreatedOK(final Dataset dataset,
		final Function<Dataset, ? extends Img<?>> creator, final Dimensions eDims)
	{
		assertCreatedOK(dataset, creator, eDims, dataset.firstElement().getClass());
	}

	private void assertCreatedOK(final Dataset dataset,
		final Function<Dataset, ? extends Img<?>> creator, //
		final Dimensions eDims, final Class<?> eType)
	{
		final Img<?> created = creator.apply(dataset);
		assertSame(eType, created.firstElement().getClass());
		assertSame(img(dataset).getClass(), img(created).getClass());
		assertEquals(eDims.numDimensions(), created.numDimensions());
		for (int d = 0; d < eDims.numDimensions(); d++) {
			assertEquals(eDims.dimension(d), created.dimension(d));
		}
	}

	/** Obtains wrapped {@link Img} of {@link Dataset} and {@link ImgPlus}. */
	private Img<?> img(final IterableInterval<?> image) {
		if (image instanceof Dataset) return img(((Dataset) image).getImgPlus());
		if (image instanceof ImgPlus) return img(((ImgPlus<?>) image).getImg());
		if (image instanceof Img) return (Img<?>) image;
		throw new IllegalArgumentException(//
			"No relevant Img for type: " + image.getClass().getName());
	}
}
