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

import net.imagej.ChannelCollection;
import net.imagej.Dataset;
import net.imagej.ImageJService;
import net.imagej.Position;
import net.imagej.overlay.CompositeOverlay;
import net.imagej.overlay.Overlay;
import net.imagej.overlay.OverlaySettings;

import org.scijava.object.ObjectService;
import org.scijava.util.RealRect;

/**
 * Interface for service that works with {@link Overlay}s.
 * 
 * @author Curtis Rueden
 */
public interface OverlayService extends ImageJService {

	ObjectService getObjectService();

	/**
	 * Gets a list of all {@link Overlay}s. This method is a shortcut that
	 * delegates to {@link ObjectService}.
	 */
	List<Overlay> getOverlays();

	/**
	 * Gets a list of {@link Overlay}s linked to the given {@link ImageDisplay}.
	 * If selectedOnly is true then it will gather overlays from the selected
	 * views only. Otherwise it will gather overlays from all the views.
	 */
	List<Overlay> getOverlays(ImageDisplay display, boolean selectedOnly);

	/**
	 * Gets a list of {@link Overlay}s linked to the given {@link ImageDisplay}. A
	 * shortcut for {@code getOverlays(display, false)}.
	 */
	List<Overlay> getOverlays(ImageDisplay display);

	/** Adds the list of {@link Overlay}s to the given {@link ImageDisplay}. */
	void addOverlays(ImageDisplay display, List<? extends Overlay> overlays);

	/**
	 * Removes an {@link Overlay} from the given {@link ImageDisplay}.
	 * 
	 * @param display the {@link ImageDisplay} from which the overlay should be
	 *          removed
	 * @param overlay the {@link Overlay} to remove
	 */
	void removeOverlay(ImageDisplay display, Overlay overlay);

	/**
	 * Removes an {@link Overlay} from all {@link ImageDisplay}s.
	 * 
	 * @param overlay the {@link Overlay} to remove
	 */
	void removeOverlay(Overlay overlay);

	/**
	 * Gets the bounding box for the selected overlays in the given
	 * {@link ImageDisplay}.
	 * 
	 * @param display the {@link ImageDisplay} from which the bounding box should
	 *          be computed
	 * @return the smallest bounding box encompassing all selected overlays
	 */
	RealRect getSelectionBounds(ImageDisplay display);

	OverlaySettings getDefaultSettings();

	/**
	 * Draws the outline of a given overlay in a display using the set of channel
	 * information provided.
	 */
	void drawOverlay(Overlay o, ImageDisplay display,
		ChannelCollection channelData);

	/**
	 * Draws the outline of a given overlay in a dataset using the set of channel
	 * information provided.
	 */
	void drawOverlay(Overlay o, Dataset ds, Position position,
		ChannelCollection channelData);

	/**
	 * Draws and fills the outline of a given overlay in a display using the set
	 * of channel information provided.
	 */
	void fillOverlay(Overlay o, ImageDisplay display,
		ChannelCollection channelData);

	/**
	 * Draws and fills the outline of a given overlay in a dataset using the set
	 * of channel information provided.
	 */
	void fillOverlay(Overlay o, Dataset ds, Position position,
		ChannelCollection channelData);

	/**
	 * Returns the first display associated with an overlay
	 */
	ImageDisplay getFirstDisplay(Overlay o);

	/**
	 * Returns all the displays associated with an overlay
	 */
	List<ImageDisplay> getDisplays(Overlay o);

	/**
	 * Returns the active overlay associated with a display
	 */
	Overlay getActiveOverlay(ImageDisplay disp);

	/**
	 * Returns the overlay info list associated with this service. There is one
	 * list per ImageJ context. It tracks overlay selection info and is used by
	 * any defined overlay managers.
	 */
	OverlayInfoList getOverlayInfo();

	/**
	 * Divides a CompositeOverlay into its constituent parts and registers each
	 * part with the appropriate displays. Deletes the CompositeOverlay.
	 */
	void divideCompositeOverlay(CompositeOverlay overlay);
}
