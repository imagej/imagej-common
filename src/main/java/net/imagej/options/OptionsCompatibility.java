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

import java.net.URL;

import org.scijava.log.LogService;
import org.scijava.menu.MenuConstants;
import org.scijava.options.OptionsPlugin;
import org.scijava.platform.PlatformService;
import org.scijava.plugin.Attr;
import org.scijava.plugin.Menu;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.widget.Button;

/**
 * Runs the Edit::Options::Compatibility dialog.
 * 
 * @author Barry DeZonia
 */
@Plugin(type = OptionsPlugin.class, menu = {
	@Menu(label = MenuConstants.EDIT_LABEL, weight = MenuConstants.EDIT_WEIGHT,
		mnemonic = MenuConstants.EDIT_MNEMONIC),
	@Menu(label = "Options", mnemonic = 'o'),
	@Menu(label = "Compatibility...", weight = 16) }, attrs = { @Attr(
	name = "no-legacy") })
public class OptionsCompatibility extends OptionsPlugin {

	private static final String MODE_LEGACY = "Legacy";
	private static final String MODE_MODERN = "Modern";

	@Parameter(required = false)
	private PlatformService platformService;

	@Parameter
	private LogService log;

	@Parameter(label = "Notes",
		description = "View a web page detailing the commands on this dialog",
		callback = "openWebPage", persist = false)
	private Button openWebPage;

	@Parameter(label = "Invert command", choices = { MODE_LEGACY, MODE_MODERN })
	private String invertMode = MODE_LEGACY;

	// -- OptionsMisc methods --

	public boolean isInvertModeLegacy() {
		return invertMode.equals(MODE_LEGACY);
	}

	public boolean isInvertModeModern() {
		return invertMode.equals(MODE_MODERN);
	}

	public void setInvertModeLegacy() {
		invertMode = MODE_LEGACY;
	}

	public void setInvertModeModern() {
		invertMode = MODE_MODERN;
	}

	// -- helpers --

	protected void openWebPage() {
		try {
			final String urlString =
				"http://wiki.imagej.net/ImageJ2/Documentation/Edit/Options/Compatibility";
			final URL url = new URL(urlString);
			if (platformService == null) {
				log.error("Could not open URL " + urlString);
			}
			else {
				platformService.open(url);
			}
		}
		catch (final Exception e) {
			// do nothing
		}
	}
}
