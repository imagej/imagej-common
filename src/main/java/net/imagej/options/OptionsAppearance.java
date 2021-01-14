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

package net.imagej.options;

import org.scijava.menu.MenuConstants;
import org.scijava.options.OptionsPlugin;
import org.scijava.plugin.Attr;
import org.scijava.plugin.Menu;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.util.ColorRGB;
import org.scijava.util.Colors;

/**
 * Runs the Edit::Options::Appearance dialog.
 * 
 * @author Barry DeZonia
 */
@Plugin(type = OptionsPlugin.class, menu = {
	@Menu(label = MenuConstants.EDIT_LABEL, weight = MenuConstants.EDIT_WEIGHT,
		mnemonic = MenuConstants.EDIT_MNEMONIC),
	@Menu(label = "Options", mnemonic = 'o'),
	@Menu(label = "Appearance...", weight = 10) }, attrs = { @Attr(name = "no-legacy") })
public class OptionsAppearance extends OptionsPlugin {

	@Parameter(label = "Selection color")
	private ColorRGB selectionColor = Colors.YELLOW;

	@Parameter(label = "Interpolate zoomed images")
	private boolean interpZoomedImages = false;

	@Parameter(label = "Open images at 100%")
	private boolean fullZoomImages = false;

	@Parameter(label = "Black canvas")
	private boolean blackCanvas = false;

	@Parameter(label = "No image border")
	private boolean noImageBorder = false;

	@Parameter(label = "Use inverting lookup table")
	private boolean useInvertingLUT = false;

	@Parameter(label = "Antialiased tool icons")
	private boolean antialiasedToolIcons = true;

	@Parameter(label = "Menu font size (points)", min = "0")
	private int menuFontSize = 0;

	// NOTE - this one is not part of legacy ImageJ but is a modern ImageJ
	// enhancement
	@Parameter(label = "Display fractional scales")
	private boolean displayFractionalScales = false;

	// -- OptionsAppearance methods --

	public ColorRGB getSelectionColor() {
		return selectionColor;
	}

	public boolean isInterpZoomedImages() {
		return interpZoomedImages;
	}

	public boolean isFullZoomImages() {
		return fullZoomImages;
	}

	public boolean isBlackCanvas() {
		return blackCanvas;
	}

	public boolean isNoImageBorder() {
		return noImageBorder;
	}

	public boolean isUseInvertingLUT() {
		return useInvertingLUT;
	}

	public boolean isAntialiasedToolIcons() {
		return antialiasedToolIcons;
	}

	public int getMenuFontSize() {
		return menuFontSize;
	}

	public boolean isDisplayFractionalScales() {
		return displayFractionalScales;
	}

	public void setSelectionColor(final ColorRGB c) {
		this.selectionColor = c;
	}

	public void setInterpZoomedImages(final boolean interpZoomedImages) {
		this.interpZoomedImages = interpZoomedImages;
	}

	public void setFullZoomImages(final boolean fullZoomImages) {
		this.fullZoomImages = fullZoomImages;
	}

	public void setBlackCanvas(final boolean blackCanvas) {
		this.blackCanvas = blackCanvas;
	}

	public void setNoImageBorder(final boolean noImageBorder) {
		this.noImageBorder = noImageBorder;
	}

	public void setUseInvertingLUT(final boolean useInvertingLUT) {
		this.useInvertingLUT = useInvertingLUT;
	}

	public void setAntialiasedToolIcons(final boolean antialiasedToolIcons) {
		this.antialiasedToolIcons = antialiasedToolIcons;
	}

	public void setMenuFontSize(final int menuFontSize) {
		this.menuFontSize = menuFontSize;
	}

	public void setDisplayFractionalScales(final boolean displayFractionalScales)
	{
		this.displayFractionalScales = displayFractionalScales;
	}

}
