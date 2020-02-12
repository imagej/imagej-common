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

package net.imagej;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.type.numeric.integer.IntType;

import org.junit.After;
import org.junit.Before;
import org.scijava.Context;

/**
 * Base class for {@link Dataset}-related tests.
 *
 * @author Barry DeZonia
 * @author Richard Domander
 * @author Curtis Rueden
 */
public class AbstractDatasetTest {

	protected static final int CPLANES = 2;
	protected static final int ZPLANES = 3;
	protected static final int TPLANES = 4;
	protected static final long[] DIMENSIONS = { 4, 4, CPLANES, ZPLANES, TPLANES };

	protected Context context;
	protected DatasetService datasetService;

	@Before
	public void setUp() {
		context = new Context(DatasetService.class);
		datasetService = context.service(DatasetService.class);
	}

	@After
	public void tearDown() {
		context.dispose();
	}

	// -- Internal methods --

	protected Dataset createDataset(final ImgFactory<IntType> factory) {
		final Img<IntType> img = factory.create(DIMENSIONS);
		final ImgPlus<IntType> imgPlus = new ImgPlus<>(img);
		return datasetService.create(imgPlus);
	}

	protected Dataset createPlanarDataset() {
		return createDataset(new PlanarImgFactory<>(new IntType()));
	}

	protected Dataset createNonplanarDataset() {
		return createDataset(new CellImgFactory<>(new IntType()));
	}

	protected void assertDatasetsMatch(final Dataset d, final Dataset d2,
		final Class<?> dType, final Class<?> d2Type)
	{
		assertEquals(d.numDimensions(), d2.numDimensions());
		for (int i = 0; i < d.numDimensions(); i++) {
			assertEquals(d.dimension(i), d2.dimension(i));
		}
		assertSame(d.getImgPlus().getImg().getClass(), //
			d2.getImgPlus().getImg().getClass());
		assertSame(dType, d.getType().getClass());
		assertSame(d2Type, d2.getType().getClass());
	}
}
