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

package net.imagej.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.imagej.app.ImageJApp;

import org.scijava.AbstractContextual;
import org.scijava.Context;
import org.scijava.app.AppService;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;

/**
 * This class reads launcher configuration parameters from a file and allows
 * them to be maintained.
 * 
 * @author Barry DeZonia
 */
public class ConfigFileParameters extends AbstractContextual {

	// -- public constants --

	public static final String CONFIG_FILE = "ImageJ.cfg";

	// -- private constants --

	private static final Integer MINIMUM_MEMORY = 256; // in megabytes
	private static final String SENTINEL = "ImageJ startup properties";
	private static final String MEMORY_KEY = "maxheap.mb";
	private static final String JVMARGS_KEY = "jvmargs";

	// -- private instance variables --

	@Parameter
	private AppService appService;

	@Parameter
	private LogService log;

	private final Map<String, String> dataMap;
	private final File configFile;

	// -- constructors --

	/**
	 * Constructs a ConfigFileParameters object. Loads values from the default
	 * file location.
	 */
	public ConfigFileParameters(final Context context) {
		this(context, null);
	}

	/**
	 * Constructs a ConfigFileParameters object. Uses filename for loading/saving
	 * its values.
	 */
	public ConfigFileParameters(final Context context, final File configFile) {
		setContext(context);
		this.dataMap = new HashMap<>();
		this.configFile = configFile == null ? defaultConfigFile() : configFile;
		initialize();
	}

	// -- public interface --

	/**
	 * Returns the value of the number of megabytes of ram to allocate that is
	 * specified in the launcher config file. Will never return less than a
	 * minimum number (currently 256).
	 */
	public int getMemoryInMB() {
		final String memVal = dataMap.get(MEMORY_KEY);
		Integer val = 0;
		try {
			val = Integer.parseInt(memVal);
		}
		catch (final NumberFormatException e) {
			log.warn("Launcher configuration file " + configFile + " has key " +
				MEMORY_KEY + " that is not in an integer format");
		}
		if (val < MINIMUM_MEMORY) val = MINIMUM_MEMORY;
		return val;
	}

	/**
	 * Sets the value of the number of megabytes of ram to allocate. Saves this
	 * value in the launcher config file. Will not allow values less than a
	 * minimum number (currently 256).
	 */
	public void setMemoryInMB(final int numMegabytes) {
		Integer memory = numMegabytes;
		if (memory < MINIMUM_MEMORY) {
			memory = MINIMUM_MEMORY;
			log.warn("Max Java heap size can be no smaller than " + MINIMUM_MEMORY +
				" megabytes.");
		}
		dataMap.put(MEMORY_KEY, memory.toString());
		save();
	}

	/**
	 * Gets the value associated with the "jvmargs" key in the launcher
	 * configuration file.
	 */
	public String getJvmArgs() {
		return dataMap.get(JVMARGS_KEY);
	}

	/**
	 * Sets the value associated with the "jvmargs" key in the launcher
	 * configuration file.
	 */
	public void setJvmArgs(final String args) {
		String value = args;
		if (args == null) value = "";
		dataMap.put(JVMARGS_KEY, value);
		save();
	}

	// -- private helpers --

	/** Finds the default name/location of the launcher config file. */
	private File defaultConfigFile() {
		final File directory = appService.getApp().getBaseDirectory();
		return new File(directory, CONFIG_FILE);
	}

	/**
	 * Initializes launcher config values. If possible loads values from launcher
	 * config file. If launcher config file does not exist or is outdated or
	 * faulty this method will save a valid set of parameters in the launcher
	 * config file.
	 */
	private void initialize() {
		setDefaultValues(dataMap);
		if (isLegacyConfigFile(configFile)) {
			loadLegacyConfigValues(dataMap, configFile);
		}
		else loadModernConfigValues(dataMap, configFile);
	}

	/** Saves current values to the launcher config file. */
	private void save() {
		saveConfigValues(dataMap, configFile);
	}

	/** Initializes launcher config file values to valid defaults. */
	private void setDefaultValues(final Map<String, String> map) {
		map.clear();
		map.put(MEMORY_KEY, MINIMUM_MEMORY.toString());
		map.put(JVMARGS_KEY, "");
	}

	/**
	 * Returns true if specified config file is an old legacy style launcher
	 * config file.
	 */
	private boolean isLegacyConfigFile(final File file) {
		if (!file.exists()) return false;
		try {
			final FileInputStream fstream = new FileInputStream(file);
			final DataInputStream din = new DataInputStream(fstream);
			final InputStreamReader in = new InputStreamReader(din);
			final BufferedReader br = new BufferedReader(in);
			final String firstLine = br.readLine();
			in.close();
			return !firstLine.contains(SENTINEL);
		}
		catch (final Exception e) {
			return false;
		}
	}

	/** Loads launcher config file values from an old legacy style file. */
	private boolean loadLegacyConfigValues(final Map<String, String> map,
		final File file)
	{
		if (!file.exists()) return false;
		try {
			final FileInputStream fstream = new FileInputStream(file);
			final DataInputStream din = new DataInputStream(fstream);
			final InputStreamReader in = new InputStreamReader(din);
			final BufferedReader br = new BufferedReader(in);
			// ignore first line: a path ... something like "."
			br.readLine();
			// ignore second line: path to java.exe
			br.readLine();
			// everything we want is on third line
			final String argString = br.readLine();
			in.close();
			final Integer memSize = memorySize(argString);
			final String jvmArgs = jvmArgs(argString);
			map.put(MEMORY_KEY, memSize.toString());
			map.put(JVMARGS_KEY, jvmArgs);
			return true;
		}
		catch (final Exception e) {
			log.warn("Couldd not load legacy launcher config file " + file);
			return false;
		}
	}

	/** Loads launcher config file values from a modern ImageJ style file. */
	private boolean loadModernConfigValues(final Map<String, String> map,
		final File file)
	{
		if (!file.exists()) return false;
		try {
			final FileInputStream fstream = new FileInputStream(file);
			final DataInputStream din = new DataInputStream(fstream);
			final InputStreamReader in = new InputStreamReader(din);
			final BufferedReader br = new BufferedReader(in);
			final Pattern keyValuePairPattern =
				Pattern.compile("\\s*(.*)\\s*=\\s*(.*)");
			// skip first line : sentinel
			br.readLine();
			while (br.ready()) {
				final String s = br.readLine();
				if (s.trim().startsWith("#")) continue;
				final Matcher matcher = keyValuePairPattern.matcher(s);
				if (matcher.matches()) {
					final String key = matcher.group(1).trim();
					final String value = matcher.group(2).trim();
					map.put(key, value);
				}

			}
			br.close();
			return true;
		}
		catch (final IOException e) {
			log.warn("Could not load launcher config file " + file);
			return false;
		}
	}

	/**
	 * Writes launcher config values to a modern ImageJ style launcher config
	 * file.
	 */
	private void
		saveConfigValues(final Map<String, String> map, final File file)
	{
		try {
			final FileOutputStream fos = new FileOutputStream(file);
			final OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF8");
			final BufferedWriter out = new BufferedWriter(osw);
			out.write("#" + SENTINEL + " (" + getVersion() + ")");
			out.newLine();
			for (final String key : map.keySet()) {
				String value = map.get(key);
				// make sure we don't write out something that breaks file structure
				value = value.replaceAll("\n", "");
				value = value.replaceAll("\r", "");
				out.write(key + " = " + map.get(key));
				out.newLine();
			}
			out.close();
		}
		catch (final IOException e) {
			log.warn("Could not save launcher config file values to " + file);
		}
	}

	/**
	 * Returns the number of megabytes specified in a text line from a legacy
	 * launcher config file (3rd line).
	 */
	private int memorySize(final String argList) {
		// NB - In old IJ1 cfg files heap use is encoded as a single argument:
		// "-XmxNNNNNNm" where N are numbers.
		// Find this argument and parse it
		final String[] args = argList.split("\\s+");
		for (final String arg : args) {
			if (arg.startsWith("-Xmx")) {
				String numString = arg.substring(4);
				numString = numString.substring(0, numString.length() - 1);
				try {
					return Integer.parseInt(numString);
				}
				catch (final NumberFormatException e) {
					return MINIMUM_MEMORY;
				}
			}
		}
		return MINIMUM_MEMORY;
	}

	/**
	 * Returns a string containing all the command line arguments from a legacy
	 * launcher config file (3rd line). Ignores memory specification as that is
	 * handled by memorySize().
	 */
	private String jvmArgs(final String argList) {

		// NB - From old IJ1 cfg we return the list of arguments except the heap
		// specification (which we've handled elsewhere) and the name of the
		// main class to launch (since ours will be different).

		String value = "";
		final String[] args = argList.split("\\s+");
		for (final String arg : args) {
			if (arg.startsWith("-Xmx")) continue; // skip heap size specification
			if (arg.equals("ij.ImageJ")) continue; // skip name of main class
			// if here then we are interested in this parameter
			if (value.length() > 0) value += " ";
			value += arg;
			// TODO This last addition could include class path info. Is that a
			// problem? We may not want the legacy file's class path info.
		}
		return value;
	}

	private String getVersion() {
		return appService.getApp(ImageJApp.NAME).getVersion();
	}

}
