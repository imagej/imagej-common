/*
 * #%L
 * ImageJ2 software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2021 ImageJ2 developers.
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

package net.imagej.ui;

import net.imagej.options.OptionsMemoryAndThreads;

import org.scijava.app.AppService;
import org.scijava.app.StatusService;
import org.scijava.display.event.input.MsPressedEvent;
import org.scijava.event.EventHandler;
import org.scijava.options.OptionsService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

/**
 * Default service for managing ImageJ user interfaces.
 * 
 * @author Curtis Rueden
 */
@Plugin(type = Service.class)
public class DefaultImageJUIService extends AbstractService implements
	ImageJUIService
{

	@Parameter
	private OptionsService optionsService;

	@Parameter
	private StatusService statusService;

	@Parameter
	private AppService appService;

	// -- Event handlers --

	@EventHandler
	protected void onEvent(final MsPressedEvent event) {
		if (event.getDisplay() == null) {
			final OptionsMemoryAndThreads options =
				optionsService.getOptions(OptionsMemoryAndThreads.class);
			if (options.isRunGcOnClick()) System.gc();
			// mouse press in main application frame;
			// show application info in status bar
			statusService.showStatus(appService.getApp().getInfo(true));
		}
	}

}
