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

package net.imagej.table;

import net.imagej.ImgPlus;
import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.real.DoubleType;

import org.scijava.table.AbstractTable;
import org.scijava.table.DefaultDoubleTable;
import org.scijava.table.DoubleColumn;

/**
 * Default implementation of {@link ResultsTable}.
 * 
 * @author Curtis Rueden
 */
public class DefaultResultsTable extends DefaultDoubleTable
	implements ResultsTable
{

	/** Creates an empty results table. */
	public DefaultResultsTable() {
		super();
	}

	/** Creates a results table with the given row and column dimensions. */
	public DefaultResultsTable(final int columnCount, final int rowCount) {
		super(columnCount, rowCount);
	}

	// -- ResultsTable methods --

	@Override
	public ImgPlus<DoubleType> img() {
		final Img<DoubleType> img = new ResultsImg(this);
		final AxisType[] axes = { Axes.X, Axes.Y };
		final String name = "Results";
		final ImgPlus<DoubleType> imgPlus =
			new ImgPlus<>(img, name, axes);
		// TODO: Once ImgPlus has a place for row & column labels, add those too.
		return imgPlus;
	}

}
