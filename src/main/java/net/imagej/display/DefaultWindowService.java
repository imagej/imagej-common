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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scijava.MenuEntry;
import org.scijava.MenuPath;
import org.scijava.command.CommandInfo;
import org.scijava.display.Display;
import org.scijava.display.event.DisplayActivatedEvent;
import org.scijava.display.event.DisplayCreatedEvent;
import org.scijava.display.event.DisplayDeletedEvent;
import org.scijava.event.EventHandler;
import org.scijava.event.EventService;
import org.scijava.menu.MenuConstants;
import org.scijava.menu.MenuService;
import org.scijava.module.ModuleInfo;
import org.scijava.module.ModuleService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.service.AbstractService;
import org.scijava.service.Service;

//TODO - DefaultRecentFileService, DefaultWindowService, and DefaultLUTService
//all build menus dynamically (see createInfo()). We may be able to abstract a
//helper class out of these that can be used by them and future services.

/**
 * Default service for keeping track of open windows, including management of
 * the Windows menu.
 * 
 * @author Grant Harris
 */
@Plugin(type = Service.class)
public final class DefaultWindowService extends AbstractService implements
	WindowService
{

	// max name width constants
	private static final int MAX_LEADER_SIZE = 10;
	private static final int MAX_TRAILER_SIZE = 26;

	@Parameter
	private MenuService menuService;

	@Parameter
	private ModuleService moduleService;

	@Parameter
	private EventService eventService;

	private List<String> openWindows;

	private Map<String, ModuleInfo> windowModules;

	/*
	 * order in menu, 'weight'
	 */

	private int order = 1000;

	// -- WindowService methods --

	@Override
	public MenuService getMenuService() {
		return menuService;
	}

	@Override
	public ModuleService getModuleService() {
		return moduleService;
	}

	@Override
	public EventService getEventService() {
		return eventService;
	}

	@Override
	public void add(final String displayName) {
		final ModuleInfo info = windowModules.get(displayName);
		if (info != null) { // already present
			updateInfo(displayName);
		}
		else {
			windowModules.put(displayName, createInfo(displayName));
			openWindows.add(displayName);
		}
	}

	@Override
	public boolean remove(final String displayName) {
		final ModuleInfo info = windowModules.remove(displayName);
		if (info != null) {
			moduleService.removeModule(info);
		}
		return openWindows.remove(displayName);
	}

	@Override
	public void clear() {
		openWindows.clear();
		moduleService.removeModules(windowModules.values());
		windowModules.clear();
	}

	@Override
	public List<String> getOpenWindows() {
		return Collections.unmodifiableList(openWindows);
	}

	// -- Service methods --

	@Override
	public void initialize() {
		openWindows = new ArrayList<>();
		windowModules = new HashMap<>();
	}

	// -- Event handlers --

	@EventHandler
	protected void onEvent(final DisplayCreatedEvent event) {
		final Display<?> display = event.getObject();
		add(display.getName());
	}

	@EventHandler
	protected void onEvent(final DisplayActivatedEvent event) {
		final Display<?> display = event.getDisplay();
		// TODO - needs checkbox menu functionality
		//setActiveWindow(display);
	}

	@EventHandler
	protected void onEvent(final DisplayDeletedEvent event) {
		final Display<?> display = event.getObject();
		remove(display.getName());
	}

	// -- Helper methods --

	/** Creates a {@link ModuleInfo} to reopen data at the given path. */
	private ModuleInfo createInfo(final String displayName) {
		final CommandInfo info = new CommandInfo(SelectWindow.class.getName());

		// hard code path to open as a preset
		final HashMap<String, Object> presets = new HashMap<>();
		presets.put("displayToSelect", displayName);
		info.setPresets(presets);

		// set menu path
		final MenuPath menuPath = new MenuPath();
		menuPath.add(new MenuEntry(MenuConstants.WINDOW_LABEL));
		final MenuEntry leaf = new MenuEntry(shortPath(displayName));
		menuPath.add(leaf);
		info.setMenuPath(menuPath);

		// set menu position
		leaf.setWeight(order++); // TODO - do this properly

		// register the module with the module service
		moduleService.addModule(info);

		return info;
	}

	private void updateInfo(final String path) {
		final ModuleInfo info = windowModules.get(path);

		// TODO - update module weights

		// notify interested parties
		info.update(eventService);
	}

	// TODO - BDZ - this is a first attempt. Friendlier algorithms must exist.
	
	/** Shortens the given path to ensure it conforms to a maximum length. */
	private String shortPath(final String path) {
		String newPath = path;
		if (path != null && path.length() > MAX_LEADER_SIZE + 3 + MAX_TRAILER_SIZE)
		{
			newPath = path.substring(0, MAX_LEADER_SIZE);
			newPath += "...";
			int p = path.length() - MAX_TRAILER_SIZE - 1;
			newPath += path.substring(p, p + MAX_TRAILER_SIZE);
		}
		return newPath;
	}

}
