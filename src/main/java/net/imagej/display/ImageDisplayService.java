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

package net.imagej.display;

import java.util.List;

import net.imagej.Data;
import net.imagej.Dataset;
import net.imagej.ImageJService;
import net.imagej.Position;

import org.scijava.display.DisplayService;
import org.scijava.event.EventService;
import org.scijava.plugin.PluginService;

/**
 * Interface for services that work with {@link ImageDisplay}s.
 * 
 * @author Barry DeZonia
 * @author Curtis Rueden
 * @author Grant Harris
 */
public interface ImageDisplayService extends ImageJService {

	EventService getEventService();

	PluginService getPluginService();

	DisplayService getDisplayService();

	/** Creates a new {@link DataView} wrapping the given data object. */
	DataView createDataView(Data data);

	/**
	 * Gets the list of available {@link DataView}s. The list will contain one
	 * uninitialized instance of each {@link DataView} implementation known to the
	 * {@link PluginService}.
	 */
	List<? extends DataView> getDataViews();

	/** Gets the currently active {@link ImageDisplay}. */
	ImageDisplay getActiveImageDisplay();

	/**
	 * Gets the active {@link Dataset}, if any, of the currently active
	 * {@link ImageDisplay}.
	 */
	Dataset getActiveDataset();

	/**
	 * Gets the active {@link DatasetView}, if any, of the currently active
	 * {@link ImageDisplay}.
	 */
	DatasetView getActiveDatasetView();
	
	/** 
	 * Gets the active {@link Position}, if any, of the currently active
	 * {@link ImageDisplay}.
	 */
	Position getActivePosition();

	/**
	 * Gets the active {@link Dataset}, if any, of the given {@link ImageDisplay}.
	 */
	Dataset getActiveDataset(ImageDisplay display);

	/**
	 * Gets the active {@link DatasetView}, if any, of the given
	 * {@link ImageDisplay}.
	 */
	DatasetView getActiveDatasetView(ImageDisplay display);

	/** Gets a list of all available {@link ImageDisplay}s. */
	List<ImageDisplay> getImageDisplays();
	
	/** 
	 * Gets the active {@link Position}, if any, of the active
	 * {@link DatasetView} in the given {@link ImageDisplay}.
	 */
	Position getActivePosition(ImageDisplay display);

}
