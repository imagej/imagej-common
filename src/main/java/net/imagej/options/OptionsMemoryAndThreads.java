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

import net.imagej.config.ConfigFileParameters;

import org.scijava.menu.MenuConstants;
import org.scijava.options.OptionsPlugin;
import org.scijava.plugin.Attr;
import org.scijava.plugin.Menu;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Runs the Edit::Options::Memory &amp; Threads dialog.
 * 
 * @author Barry DeZonia
 */
@Plugin(type = OptionsPlugin.class, menu = {
	@Menu(label = MenuConstants.EDIT_LABEL, weight = MenuConstants.EDIT_WEIGHT,
		mnemonic = MenuConstants.EDIT_MNEMONIC),
	@Menu(label = "Options", mnemonic = 'o'),
	@Menu(label = "Memory & Threads...", weight = 12) }, attrs = { @Attr(name = "no-legacy") })
public class OptionsMemoryAndThreads extends OptionsPlugin {

	// -- instance variables that are Parameters --

	@Parameter(label = "Maximum memory (MB)", persist = false)
	private int maxMemory = 0;

	@Parameter(label = "Parallel threads for stacks")
	private int stackThreads = 2;

	@Parameter(label = "Keep multiple undo buffers")
	private boolean multipleBuffers = false;

	@Parameter(label = "Run garbage collector on status bar click")
	private boolean runGcOnClick = true;

	// -- private instance variables --

	private ConfigFileParameters params;

	// -- OptionsMemoryAndThreads methods --

	/** Loads the instance variable fields from persistent storage. */
	@Override
	public void load() {
		super.load();
		loadMaxMemory();
	}

	/** Saves the instance variable fields to persistent storage. */
	@Override
	public void save() {
		super.save();
		saveMaxMemory();
	}

	/**
	 * Returns the number of megabytes of memory that should be allocated for use
	 * by ImageJ.
	 */
	public int getMaxMemory() {
		return maxMemory;
	}

	/**
	 * Returns the number of stack threads that should be allocated for use by
	 * ImageJ.
	 */
	public int getStackThreads() {
		return stackThreads;
	}

	/** Returns true if ImageJ will maintain multiple undo buffers. */
	public boolean isMultipleBuffers() {
		return multipleBuffers;
	}

	/**
	 * Returns true if ImageJ will run the garbage collector when user clicks on
	 * the status area.
	 */
	public boolean isRunGcOnClick() {
		return runGcOnClick;
	}

	/**
	 * Sets the number of megabytes of memory that should be allocated for use by
	 * ImageJ.
	 */
	public void setMaxMemory(final int maxMemory) {
		this.maxMemory = maxMemory;
		saveMaxMemory();
	}

	/**
	 * Sets the number of stack threads that should be allocated for use by
	 * ImageJ.
	 */
	public void setStackThreads(final int stackThreads) {
		this.stackThreads = stackThreads;
	}

	/** Sets whether ImageJ will maintain multiple undo buffers. */
	public void setMultipleBuffers(final boolean multipleBuffers) {
		this.multipleBuffers = multipleBuffers;
	}

	/**
	 * Sets whether ImageJ will run the garbage collector when user clicks on the
	 * status area.
	 */
	public void setRunGcOnClick(final boolean runGcOnClick) {
		this.runGcOnClick = runGcOnClick;
	}

	// -- private helpers --

	/** Loads the maxMemory instance variable from persistent storage. */
	private void loadMaxMemory() {
		maxMemory = params().getMemoryInMB();
	}

	/** Saves the maxMemory instance variable to persistent storage. */
	private void saveMaxMemory() {
		if (maxMemory == params().getMemoryInMB()) return;
		params().setMemoryInMB(maxMemory);
	}

	private ConfigFileParameters params() {
		if (params == null) {
			params = new ConfigFileParameters(getContext());
		}
		return params;
	}
}
