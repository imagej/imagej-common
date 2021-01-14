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

package net.imagej.ui.dnd;

import java.io.File;
import java.io.IOException;

import net.imagej.display.ImageDisplay;
import net.imagej.lut.LUTService;
import net.imglib2.display.ColorTable;

import org.scijava.display.Display;
import org.scijava.display.DisplayService;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.dnd.AbstractDragAndDropHandler;
import org.scijava.ui.dnd.DragAndDropHandler;

/**
 * Drag-and-drop handler for {@link ColorTable} files.
 * 
 * @author Barry DeZonia
 * @author Curtis Rueden
 */
@Plugin(type = DragAndDropHandler.class)
public class LUTFileDragAndDropHandler extends AbstractDragAndDropHandler<File>
{

	@Parameter(required = false)
	private LUTService lutService;

	@Parameter(required = false)
	private DisplayService displayService;

	@Parameter(required = false)
	private LogService log;

	// -- DragAndDropHandler methods --

	@Override
	public boolean supports(final File file) {
		if (!super.supports(file)) return false;

		// verify that the file contains a color table
		if (lutService == null) return false;
		return lutService.isLUT(file);
	}

	@Override
	public boolean supportsDisplay(final Display<?> display) {
		return display == null || display instanceof ImageDisplay;
	}

	@Override
	public boolean drop(final File file, final Display<?> display) {
		if (lutService == null || displayService == null) return false;
		check(file, display);
		if (file == null) return true; // trivial case

		final ImageDisplay imageDisplay = (ImageDisplay) display;

		// load color table
		final ColorTable colorTable = loadLUT(file);
		if (colorTable == null) return false;

		if (display == null) {
			// create a new display for the LUT
			displayService.createDisplay(getBaseName(file), colorTable);
		}
		else {
			// apply the LUT to the specified display
			lutService.applyLUT(colorTable, imageDisplay);
		}
		return true;
	}

	// -- Typed methods --

	@Override
	public Class<File> getType() {
		return File.class;
	}

	// -- Helper methods --

	private ColorTable loadLUT(final File file) {

		final ColorTable colorTable;
		try {
			colorTable = lutService.loadLUT(file);
		}
		catch (final IOException exc) {
			if (log != null) log.error("Error opening LUT: " + file, exc);
			return null;
		}
		return colorTable;
	}

	private String getBaseName(final File file) {
		final String name = file.getName();
		return name.toLowerCase().endsWith(".lut") ? name.substring(0, name
			.length() - 4) : name;
	}

}
