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

package net.imagej.display;

import net.imagej.Data;
import net.imagej.overlay.Overlay;

import org.scijava.plugin.Plugin;

/**
 * A view into an {@link Overlay}, for use with a {@link ImageDisplay}.
 * 
 * @author Curtis Rueden
 */
@Plugin(type = DataView.class)
public class DefaultOverlayView extends AbstractDataView implements
	OverlayView
{

	// -- DataView methods --

	@Override
	public boolean isCompatible(final Data data) {
		return data != null && Overlay.class.isAssignableFrom(data.getClass());
	}

	@Override
	public Overlay getData() {
		return (Overlay) super.getData();
	}

	@Override
	public int getPreferredWidth() {
		// NB: No need to report preferred overlay width.
		return 0;
	}

	@Override
	public int getPreferredHeight() {
		// NB: No need to report preferred overlay height.
		return 0;
	}

	@Override
	public void update() {
		// NB: No action needed.
	}

	@Override
	public void rebuild() {
		// NB: No action needed.
	}

}
