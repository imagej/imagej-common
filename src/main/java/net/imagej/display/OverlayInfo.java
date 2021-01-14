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

import net.imagej.overlay.Overlay;

/**
 * This class defines what information is stored in an {@link OverlayInfoList}.
 * There is one info list per {@link OverlayService}.
 *  
 * @author Barry DeZonia
 *
 */
public class OverlayInfo {
	private Overlay overlay;
	private boolean selected;
	
	public OverlayInfo(Overlay o) {
		this(o,false);
	}
	
	public OverlayInfo(Overlay overlay, boolean selected) {
		this.overlay = overlay;
		this.selected = selected;
	}
	
	public Overlay getOverlay() { return overlay; }

	public boolean isSelected() { return selected; }
	
	public void setSelected(boolean b) { selected = b; }
	
	@Override
	public String toString() {
		if (overlay.getName() != null)
			return overlay.getName();
		String xVal = String.format("x=%07.1f", overlay.realMin(0));
		String yVal = String.format("y=%07.1f", overlay.realMin(1));
		StringBuilder builder = new StringBuilder();
		builder.append(xVal);
		builder.append(", ");
		builder.append(yVal);
		return builder.toString();
	}
}

