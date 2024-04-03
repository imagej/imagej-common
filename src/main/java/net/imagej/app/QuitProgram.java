/*
 * #%L
 * ImageJ2 software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2024 ImageJ2 developers.
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

import net.imagej.display.WindowService;
import net.imagej.options.OptionsMisc;

import org.scijava.app.StatusService;
import org.scijava.command.Command;
import org.scijava.command.ContextCommand;
import org.scijava.menu.MenuConstants;
import org.scijava.options.OptionsService;
import org.scijava.plugin.Attr;
import org.scijava.plugin.Menu;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.DialogPrompt;
import org.scijava.ui.UIService;

/**
 * Quits ImageJ.
 * 
 * @author Grant Harris
 * @author Barry DeZonia
 * @author Curtis Rueden
 */
@Plugin(type = Command.class, label = "Quit",
	iconPath = "/icons/commands/door_in.png", menu = {
		@Menu(label = MenuConstants.FILE_LABEL, weight = MenuConstants.FILE_WEIGHT,
			mnemonic = MenuConstants.FILE_MNEMONIC),
		@Menu(label = "Quit", weight = Double.MAX_VALUE, mnemonic = 'q',
			accelerator = "^Q") }, attrs = { @Attr(name = "no-legacy"),
		@Attr(name = "app-command") })
public class QuitProgram extends ContextCommand {

	public static final String MESSAGE = "Quit ImageJ?";

	@Parameter(required = false)
	private StatusService statusService;

	@Parameter(required = false)
	private WindowService windowService;

	@Parameter(required = false)
	private UIService uiService;

	@Parameter(required = false)
	private OptionsService optionsService;

	@Override
	public void run() {
		if (windowService != null && windowService.getOpenWindows().size() > 0) {
			if (!promptForQuit()) {
				return;
			}

			// TODO - save existing data
			// TODO - close windows
		}
		if (statusService != null) {
			statusService.showStatus("Quitting...");
		}
		boolean exitWhenQuitting = false;
		if (optionsService != null) {
			final OptionsMisc opts = optionsService.getOptions(OptionsMisc.class);
			exitWhenQuitting = opts.isExitWhenQuitting();
		}
		getContext().dispose();
		if (exitWhenQuitting) System.exit(0);
	}

	private boolean promptForQuit() {
		if (uiService == null) return true;
		final DialogPrompt.Result result =
			uiService.showDialog(MESSAGE, "Quit",
				DialogPrompt.MessageType.QUESTION_MESSAGE,
				DialogPrompt.OptionType.YES_NO_OPTION);
		return result == DialogPrompt.Result.YES_OPTION;
	}

}
