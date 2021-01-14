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

package net.imagej.animation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.imagej.Data;
import net.imagej.display.ImageDisplay;
import net.imagej.event.DataRestructuredEvent;

import org.scijava.app.StatusService;
import org.scijava.display.Display;
import org.scijava.display.event.DisplayDeletedEvent;
import org.scijava.display.event.input.KyPressedEvent;
import org.scijava.event.EventHandler;
import org.scijava.event.EventService;
import org.scijava.input.KeyCode;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

/**
 * Default service for working with {@link Animation}s.
 * 
 * @author Curtis Rueden
 */
@Plugin(type = Service.class)
public class DefaultAnimationService extends AbstractService implements
	AnimationService
{

	private static final String STARTED_STATUS =
		"Animation started. Press '\\' or ESC to stop.";
	private static final String STOPPED_STATUS =
		"Animation stopped. Press '\\' to resume.";
	private static final String ALL_STOPPED_STATUS = "All animations stopped.";

	@Parameter
	private EventService eventService;

	@Parameter
	private StatusService statusService;

	private Map<ImageDisplay, Animation> animations;

	// -- AnimationService methods --

	@Override
	public EventService getEventService() {
		return eventService;
	}

	@Override
	public void toggle(final ImageDisplay display) {
		if (getAnimation(display).isActive()) stop(display);
		else start(display);
	}

	@Override
	public void start(final ImageDisplay display) {
		getAnimation(display).start();
		statusService.showStatus(STARTED_STATUS);
	}

	@Override
	public void stop(final ImageDisplay display) {
		final Animation animation = animations.get(display);
		if (animation != null) {
			animation.stop();
			statusService.showStatus(STOPPED_STATUS);
		}
	}

	@Override
	public void stopAll() {
		for (final Animation animation : animations.values()) {
			animation.stop();
		}
		statusService.showStatus(ALL_STOPPED_STATUS);
	}

	@Override
	public Animation getAnimation(final ImageDisplay display) {
		Animation animation = animations.get(display);
		if (animation == null) {
			// animation did not already exist; create it
			animation = new Animation(display);
			animations.put(display, animation);
		}
		return animation;
	}

	// -- Service methods --

	@Override
	public void initialize() {
		animations = new ConcurrentHashMap<>();
	}

	// -- Disposable methods --

	@Override
	public void dispose() {
		stopAll();
	}

	// -- Event handlers --

	/** Stops animation if ESC key is pressed. */
	@EventHandler
	protected void onEvent(final KyPressedEvent event) {
		final ImageDisplay imageDisplay = toImageDisplay(event.getDisplay());
		if (imageDisplay == null) return;
		if (event.getCode() == KeyCode.ESCAPE) stop(imageDisplay);
	}

	/** Stops animation if display has been deleted. */
	@EventHandler
	protected void onEvent(final DisplayDeletedEvent event) {
		final ImageDisplay imageDisplay = toImageDisplay(event.getObject());
		if (imageDisplay == null) return;
		stop(imageDisplay);
		animations.remove(imageDisplay);
	}

	/** Stops animation of displays whose {@link Data} have been restructured. */
	@EventHandler
	protected void onEvent(final DataRestructuredEvent event) {
		final Data data = event.getObject();
		for (final Animation animation : animations.values()) {
			final ImageDisplay display = animation.getDisplay();
			if (display.isDisplaying(data)) stop(display);
		}
	}

	// -- Helper methods --

	private ImageDisplay toImageDisplay(final Display<?> display) {
		if (!(display instanceof ImageDisplay)) return null;
		return (ImageDisplay) display;
	}

}
