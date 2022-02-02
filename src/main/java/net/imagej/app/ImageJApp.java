/*
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

package net.imagej.app;

import org.scijava.Priority;
import org.scijava.app.AbstractApp;
import org.scijava.app.App;
import org.scijava.command.CommandService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Application metadata about ImageJ2.
 * 
 * @author Curtis Rueden
 * @see org.scijava.app.AppService
 */
@Plugin(type = App.class, name = ImageJApp.NAME, priority = ImageJApp.PRIORITY)
public class ImageJApp extends AbstractApp {

	public static final String NAME = "ImageJ2";
	public static final double PRIORITY = Priority.HIGH_PRIORITY;

	@Parameter
	private CommandService commandService;

	@Override
	public String getGroupId() {
		return "net.imagej";
	}

	@Override
	public String getArtifactId() {
		return "imagej-common";
	}

	@Override
	public void about() {
		commandService.run(AboutImageJ.class, true);
	}

	@Override
	public void prefs() {
		commandService.run(Preferences.class, true);
	}

	@Override
	public void quit() {
		commandService.run(QuitProgram.class, true);
	}

}
