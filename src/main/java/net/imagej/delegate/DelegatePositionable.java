/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2016 Board of Regents of the University of
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

package net.imagej.delegate;

import net.imglib2.Localizable;
import net.imglib2.Positionable;

/**
 * A {@link Positionable} backed by another {@link Positionable}.
 *
 * @author Curtis Rueden
 */
public interface DelegatePositionable<D extends Positionable> extends
	Positionable, DelegateEuclideanSpace<D>
{

	@Override
	default void fwd(final int d) {
		delegate().fwd(d);
	}

	@Override
	default void bck(final int d) {
		delegate().bck(d);
	}

	@Override
	default void move(final int distance, final int d) {
		delegate().move(distance, d);
	}

	@Override
	default void move(final long distance, final int d) {
		delegate().move(distance, d);
	}

	@Override
	default void move(final Localizable localizable) {
		delegate().move(localizable);
	}

	@Override
	default void move(final int[] distance) {
		delegate().move(distance);
	}

	@Override
	default void move(final long[] distance) {
		delegate().move(distance);
	}

	@Override
	default void setPosition(final Localizable localizable) {
		delegate().setPosition(localizable);
	}

	@Override
	default void setPosition(final int[] position) {
		delegate().setPosition(position);
	}

	@Override
	default void setPosition(final long[] position) {
		delegate().setPosition(position);
	}

	@Override
	default void setPosition(final int position, final int d) {
		delegate().setPosition(position, d);
	}

	@Override
	default void setPosition(final long position, final int d) {
		delegate().setPosition(position, d);
	}
}
