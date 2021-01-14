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

package net.imagej.render;

/**
 * A dummy implementation of the TextRenderer interface. Necessary to allow any
 * UI that does not support text rendering to still initialize correctly.
 * 
 * @author Barry DeZonia
 */
public class DummyTextRenderer implements TextRenderer {

	private static final String MSG = "Dummy text renderer is not functional";

	@Override
	public void renderText(final String text) {
		throw new UnsupportedOperationException(MSG);
	}

	@Override
	public int getPixelsWidth() {
		throw new UnsupportedOperationException(MSG);
	}

	@Override
	public int getPixelsHeight() {
		throw new UnsupportedOperationException(MSG);
	}

	@Override
	public int[] getPixels() {
		throw new UnsupportedOperationException(MSG);
	}

	@Override
	public void setFontFamily(final FontFamily family) {
		throw new UnsupportedOperationException(MSG);
	}

	@Override
	public FontFamily getFontFamily() {
		throw new UnsupportedOperationException(MSG);
	}

	@Override
	public void setFontStyle(final FontStyle style) {
		throw new UnsupportedOperationException(MSG);
	}

	@Override
	public FontStyle getFontStyle() {
		throw new UnsupportedOperationException(MSG);
	}

	@Override
	public void setFontSize(final int size) {
		throw new UnsupportedOperationException(MSG);
	}

	@Override
	public int getFontSize() {
		throw new UnsupportedOperationException(MSG);
	}

	@Override
	public void setAntialiasing(final boolean val) {
		throw new UnsupportedOperationException(MSG);
	}

	@Override
	public boolean getAntialiasing() {
		throw new UnsupportedOperationException(MSG);
	}
}
