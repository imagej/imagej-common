/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2015 Board of Regents of the University of
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

package net.imagej.table;

import org.scijava.util.LongArray;

/**
 * Efficient implementation of {@link Column} for {@code long} primitives.
 *
 * @author Alison Walter
 */
public class LongColumn extends LongArray implements Column<Long> {

	/** The column header. */
	private String header;

	public LongColumn() {}

	public LongColumn(final String header) {
		this.header = header;
	}

	// -- Column methods --

	@Override
	public String getHeader() {
		return header;
	}

	@Override
	public void setHeader(final String header) {
		this.header = header;
	}

	@Override
	public Class<Long> getType() {
		return Long.class;
	}

	@Override
	public void fill(final Long[] values) {
		final long[] prim = toPrimitive(values);
		this.setArray(prim);
	}

	@Override
	public void fill(final Long[] values, final int offset) {
		final long[] prim = toPrimitive(values);

		// Check if array has been initialized
		if (this.getArray() == null) this.setArray(prim);
		else {
			System.arraycopy(prim, 0, this.getArray(), offset, prim.length);
		}
	}

	// -- Helper methods --

	private long[] toPrimitive(final Long[] values) {
		final long[] prim = new long[values.length];
		for (int i = 0; i < prim.length; i++) {
			prim[i] = values[i].longValue();
		}
		return prim;
	}

}
