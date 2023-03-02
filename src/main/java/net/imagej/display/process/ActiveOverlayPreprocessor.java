/*
 * #%L
 * ImageJ2 software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2023 ImageJ2 developers.
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

package net.imagej.display.process;

import net.imagej.display.ImageDisplay;
import net.imagej.display.ImageDisplayService;
import net.imagej.display.OverlayService;
import net.imagej.overlay.Overlay;

import org.scijava.Priority;
import org.scijava.module.process.PreprocessorPlugin;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Assigns the {@link Overlay} of the active {@link ImageDisplay} when there is
 * one single unresolved {@link Overlay} parameter. Hence, rather than a dialog
 * prompting the user to choose an {@link ImageDisplay}, the {@link Overlay} of
 * the active {@link ImageDisplay} is used automatically.
 * <p>
 * In the case of more than one {@link Overlay} parameter, the active
 * {@link Overlay} is not used and instead the user must select. This behavior
 * is consistent with ImageJ v1.x.
 * </p>
 * 
 * @author Curtis Rueden
 */
@Plugin(type = PreprocessorPlugin.class, priority = Priority.VERY_HIGH_PRIORITY)
public class ActiveOverlayPreprocessor extends
	SingleInputPreprocessor<Overlay>
{

	@Parameter(required = false)
	private ImageDisplayService imageDisplayService;

	@Parameter(required = false)
	private OverlayService overlayService;

	public ActiveOverlayPreprocessor() {
		super(Overlay.class);
	}

	// -- SingleInputProcessor methods --

	@Override
	public Overlay getValue() {
		if (imageDisplayService == null) return null;
		final ImageDisplay display = imageDisplayService.getActiveImageDisplay();
		return display == null ? null : overlayService.getActiveOverlay(display);
	}

}
