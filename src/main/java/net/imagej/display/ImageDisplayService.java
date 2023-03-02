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

package net.imagej.display;

import java.util.List;

import net.imagej.Data;
import net.imagej.Dataset;
import net.imagej.ImageJService;
import net.imagej.Position;

import org.scijava.display.Display;
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

	/** Creates a new {@link DatasetView} wrapping the given dataset. */
	default DatasetView createDatasetView(final Dataset dataset) {
		DataView dataView = createDataView(dataset);
		if (!(dataView instanceof DatasetView)) {
			throw new IllegalStateException("Wrapped DataView is a " + //
				dataView.getClass().getName() + ", not a DatasetView. There might " +
				"be a rogue DataView plugin wrapping Datasets inappropriately.");
		}
		return (DatasetView) dataView;
	}

	/**
	 * Creates an {@link ImageDisplay} for the given data object. This is a more
	 * specific type-safe version of {@link DisplayService#createDisplay(Object)}.
	 * 
	 * @param data The data object for which a display should be created. The
	 *          object is wrapped to a {@link DataView} and added to a new
	 *          {@link ImageDisplay}.
	 * @return Newly created {@link ImageDisplay} containing the given data
	 *         object.
	 * @see DisplayService#createDisplay(Object)
	 */
	default ImageDisplay createImageDisplay(Data data) {
		return createImageDisplay(null, data);
	}

	/**
	 * Creates an {@link ImageDisplay} for the given data object. This is a more
	 * specific type-safe version of {@link DisplayService#createDisplay(Object)}.
	 * 
	 * @param name The name to be assigned to the display.
	 * @param data The data object for which a display should be created. The
	 *          object is wrapped to a {@link DataView} and added to the new
	 *          {@link ImageDisplay}.
	 * @return Newly created {@link ImageDisplay} containing the given data
	 *         object.
	 * @see DisplayService#createDisplay(String, Object)
	 */
	default ImageDisplay createImageDisplay(final String name, final Data data) {
		final Display<?> display = getDisplayService().createDisplay(name, data);
		if (!(display instanceof ImageDisplay)) {
			throw new IllegalStateException("Created Display is a " + //
				display.getClass().getName() + ", not an ImageDisplay. There might " +
				"be a rogue Display plugin wrapping DataViews inappropriately.");
		}
		return (ImageDisplay) display;
	}

	/**
	 * Creates an {@link ImageDisplay} for the given {@link DataView}. This is a
	 * more specific type-safe version of
	 * {@link DisplayService#createDisplay(Object)}.
	 * 
	 * @param dataView The {@link DataView} for which a display should be created.
	 *          The view is added to the new {@link ImageDisplay}.
	 * @return Newly created {@link ImageDisplay} containing the given
	 *         {@link DataView}.
	 * @see DisplayService#createDisplay(Object)
	 */
	default ImageDisplay createImageDisplay(final DataView dataView) {
		return createImageDisplay(null, dataView);
	}

	/**
	 * Creates an {@link ImageDisplay} for the given {@link DataView}. This is a
	 * more specific type-safe version of
	 * {@link DisplayService#createDisplay(Object)}.
	 * 
	 * @param name The name to be assigned to the display.
	 * @param dataView The {@link DataView} for which a display should be created.
	 *          The view is added to the new {@link ImageDisplay}.
	 * @return Newly created {@link ImageDisplay} containing the given
	 *         {@link DataView}.
	 * @see DisplayService#createDisplay(Object)
	 */
	default ImageDisplay createImageDisplay(final String name,
		final DataView dataView)
	{
		final Display<?> display = //
			getDisplayService().createDisplay(name, dataView);
		if (!(display instanceof ImageDisplay)) {
			throw new IllegalStateException("Created Display is a " + //
				display.getClass().getName() + ", not an ImageDisplay. There might " +
				"be a rogue Display plugin wrapping DataViews inappropriately.");
		}
		return (ImageDisplay) display;
	}

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
	default Dataset getActiveDataset() {
		return getActiveDataset(getActiveImageDisplay());
	}

	/**
	 * Gets the active {@link DatasetView}, if any, of the currently active
	 * {@link ImageDisplay}.
	 */
	default DatasetView getActiveDatasetView() {
		return getActiveDatasetView(getActiveImageDisplay());
	}
	
	/** 
	 * Gets the active {@link Position}, if any, of the currently active
	 * {@link ImageDisplay}.
	 */
	default Position getActivePosition() {
		return getActivePosition(getActiveImageDisplay());
	}

	/**
	 * Gets the active {@link Dataset}, if any, of the given {@link ImageDisplay}.
	 */
	default Dataset getActiveDataset(ImageDisplay display)
	{
		final DatasetView activeDatasetView = getActiveDatasetView( display );
		return activeDatasetView == null ? null : activeDatasetView.getData();
	}

	/**
	 * Gets the active {@link DatasetView}, if any, of the given
	 * {@link ImageDisplay}.
	 */
	default DatasetView getActiveDatasetView(ImageDisplay display) {
		if (display == null) return null;
		final DataView activeView = display.getActiveView();
		if (activeView instanceof DatasetView) {
			return (DatasetView) activeView;
		}
		return null;
	}

	/** Gets a list of all available {@link ImageDisplay}s. */
	List<ImageDisplay> getImageDisplays();
	
	/** 
	 * Gets the active {@link Position}, if any, of the active
	 * {@link DatasetView} in the given {@link ImageDisplay}.
	 */
	default Position getActivePosition(ImageDisplay display) {
		if (display == null) return null;
		final DatasetView activeDatasetView = getActiveDatasetView(display);
		if(activeDatasetView == null) return null;
		return activeDatasetView.getPlanePosition();
	}

}
