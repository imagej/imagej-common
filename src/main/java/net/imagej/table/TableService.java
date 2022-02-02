/*-
 * #%L
 * ImageJ2 software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2022 ImageJ2 developers.
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

import java.util.ArrayList;
import java.util.List;

import net.imagej.Dataset;
import net.imagej.ImageJService;

import org.scijava.service.Service;
import org.scijava.table.Table;

//TODO keep deprecated version of this with net.imagej.table.Table ?

/**
 * {@link Service} for working with {@link Table}s.
 *
 * @author Alison Walter
 */
public interface TableService extends ImageJService {

	public static final String TABLE_PROPERTY = "tables";

	/**
	 * Retrieves the {@link Table}s attached to the given {@link Dataset}.
	 *
	 * @param img {@link Dataset} whose {@link Table}s are desired
	 * @return {@link Table}s associated with {@code img}
	 */
	List<Table<?, ?>> getTables(final Dataset img);

	/**
	 * Attaches the given {@link Table} to the {@link Dataset}
	 *
	 * @param table {@link Table} to be attached
	 * @param img {@link Dataset} to attach the table to
	 */
	@SuppressWarnings("unchecked")
	default void add(final Table<?, ?> table, final Dataset img) {
		if (img.getProperties().get(TABLE_PROPERTY) != null) {
			((List<Table<?, ?>>) img.getProperties().get(TABLE_PROPERTY)).add(table);
		}
		else {
			final List<Table<?, ?>> t = new ArrayList<>();
			t.add(table);
			img.getProperties().put(TABLE_PROPERTY, t);
		}
	}

	/**
	 * Clears any {@link Table}s associated with the given {@link Dataset}.
	 *
	 * @param img the {@link Dataset} whose attached {@link Table}s will be
	 *          cleared.
	 */
	default void clear(final Dataset img) {
		img.getProperties().put(TABLE_PROPERTY, null);
	}

}
