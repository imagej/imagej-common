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

/**
 * Runs the Edit::Options::Profile Plot Options dialog.
 * 
 * @author Barry DeZonia
 */
@Plugin(type = OptionsPlugin.class, menu = {
	@Menu(label = MenuConstants.EDIT_LABEL, weight = MenuConstants.EDIT_WEIGHT,
		mnemonic = MenuConstants.EDIT_MNEMONIC),
	@Menu(label = "Options", mnemonic = 'o'),
	@Menu(label = "Profile Plot Options...", weight = 4) }, attrs = { @Attr(name = "no-legacy") })
public class OptionsProfilePlot extends OptionsPlugin {

	@Parameter(label = "Width (pixels)")
	private int width = 450;

	@Parameter(label = "Height (pixels)")
	private int height = 200;

	@Parameter(label = "Minimum Y")
	private double minY = 0;

	@Parameter(label = "Maximum Y")
	private double maxY = 0;

	@Parameter(label = "Fixed y-axis scale")
	private boolean yFixedScale = false;

	@Parameter(label = "Do not save x-values")
	private boolean noSaveXValues = false;

	@Parameter(label = "Auto-close")
	private boolean autoClose = false;

	@Parameter(label = "Vertical profile")
	private boolean vertProfile = false;

	@Parameter(label = "List values")
	private boolean listValues = false;

	@Parameter(label = "Interpolate line profiles")
	private boolean interpLineProf = true;

	@Parameter(label = "Draw grid lines")
	private boolean drawGridLines = true;

	// -- OptionsProfilePlot methods --

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public double getMinY() {
		return minY;
	}

	public double getMaxY() {
		return maxY;
	}

	public boolean isYFixedScale() {
		return yFixedScale;
	}

	public boolean isNoSaveXValues() {
		return noSaveXValues;
	}

	public boolean isAutoClose() {
		return autoClose;
	}

	public boolean isVertProfile() {
		return vertProfile;
	}

	public boolean isListValues() {
		return listValues;
	}

	public boolean isInterpLineProf() {
		return interpLineProf;
	}

	public boolean isDrawGridLines() {
		return drawGridLines;
	}

	public void setWidth(final int width) {
		this.width = width;
	}

	public void setHeight(final int height) {
		this.height = height;
	}

	public void setMinY(final double minY) {
		this.minY = minY;
	}

	public void setMaxY(final double maxY) {
		this.maxY = maxY;
	}

	public void setyFixedScale(final boolean yFixedScale) {
		this.yFixedScale = yFixedScale;
	}

	public void setNoSaveXValues(final boolean noSaveXValues) {
		this.noSaveXValues = noSaveXValues;
	}

	public void setAutoClose(final boolean autoClose) {
		this.autoClose = autoClose;
	}

	public void setVertProfile(final boolean vertProfile) {
		this.vertProfile = vertProfile;
	}

	public void setListValues(final boolean listValues) {
		this.listValues = listValues;
	}

	public void setInterpLineProf(final boolean interpLineProf) {
		this.interpLineProf = interpLineProf;
	}

	public void setDrawGridLines(final boolean drawGridLines) {
		this.drawGridLines = drawGridLines;
	}

}
