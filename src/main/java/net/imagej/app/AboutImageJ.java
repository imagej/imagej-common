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

package net.imagej.app;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.imagej.ChannelCollection;
import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.DrawingTool;
import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;
import net.imagej.render.RenderingService;
import net.imagej.render.TextRenderer.TextJustification;

import org.scijava.ItemIO;
import org.scijava.app.App;
import org.scijava.app.AppService;
import org.scijava.command.Command;
import org.scijava.command.ContextCommand;
import org.scijava.display.Display;
import org.scijava.display.DisplayService;
import org.scijava.io.IOService;
import org.scijava.log.LogService;
import org.scijava.menu.MenuConstants;
import org.scijava.plugin.Attr;
import org.scijava.plugin.Menu;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.util.ColorRGB;
import org.scijava.util.Colors;
import org.scijava.util.Manifest;
import org.scijava.util.MersenneTwisterFast;

// TODO
//   Have imageX.ext image file and imageX.ext.txt metadata files
//     Metadata file lists some useful info
//     - image attribution
//     - best color to render text in
//     - recommended font size
//   This plugin loads a random image and displays info including
//     attribution text

/**
 * Displays information and credits about the ImageJ software. Note that some of
 * this code was adapted from code written by Wayne Rasband for ImageJ1.
 * 
 * @author Barry DeZonia
 */
@Plugin(type = Command.class, label = "About ImageJ...",
	iconPath = "/icons/commands/information.png", menu = {
		@Menu(label = MenuConstants.HELP_LABEL, weight = MenuConstants.HELP_WEIGHT,
			mnemonic = MenuConstants.HELP_MNEMONIC),
		@Menu(label = "About ImageJ...", weight = 43) }, headless = true, attrs = {
		@Attr(name = "no-legacy"), @Attr(name = "app-command") })
public class AboutImageJ extends ContextCommand {

	// -- parameters --

	@Parameter
	private LogService log;

	@Parameter
	private AppService appService;

	@Parameter
	private IOService ioSrv;

	@Parameter
	private DatasetService dataSrv;

	@Parameter
	private DisplayService dispSrv;

	@Parameter
	private RenderingService rendSrv;

	@Parameter(type = ItemIO.OUTPUT)
	private Display<?> display;

	// -- instance variables that are not parameters --

	private final List<String> attributionStrings = new LinkedList<>();
	private ColorRGB textColor = Colors.YELLOW;
	private final ColorRGB outlineColor = Colors.BLACK;
	private int largestFontSize = 35;
	private ChannelCollection textChannels = null;
	private ChannelCollection outlineChannels = null;

	// -- public interface --

	@Override
	public void run() {
		final Dataset dataset = createDataset();
		drawTextOverImage(dataset);
		final String title = getApp().getTitle();
		display = dispSrv.createDisplay("About " + title, dataset);
	}

	public Display<?> getDisplay() {
		return display;
	}

	// -- private helpers --

	/**
	 * Returns a merged color Dataset as a backdrop.
	 */
	private Dataset createDataset() {
		final File imageFile = getRandomAboutImagePath();
		final String source = imageFile != null ?
				imageFile.getAbsolutePath() :
				"About ImageJ&pixelType=uint16&axisTypes=X,Y&axisLengths=512,512.fake";

		final String title = "About " + getApp().getTitle();

		Dataset ds = null;
		try {
			final Object obj = ioSrv.open(source);
			if (obj instanceof Dataset) ds = (Dataset) obj;
			else {
				log.error(obj.getClass().getName() + " is not a Dataset: " + source);
			}
		}
		catch (final IOException e) {
			log.error(e);
		}

		// did we successfully load a background image?
		if (ds != null) {
			// yes we did - inspect it
			boolean validImage = true;
			validImage &= (ds.numDimensions() == 3);
			// Too restrictive? Ran into images where 3rd axis is mislabeled
			// validImage &= (ds.dimensionIndex(Axes.CHANNEL) == 2);
			if (validImage) {
				loadAttributes(imageFile);
			}
			else {
				ds = null;
			}
		}

		// Did we fail to load a valid dataset?
		if (ds == null) {
			log.warn("Could not load a 3 channel unsigned 8 bit image as backdrop");
			// make a black 3 channel 8-bit unsigned background image.
			ds =
				dataSrv.create(new long[] { 500, 500, 3 }, title, new AxisType[] {
					Axes.X, Axes.Y, Axes.CHANNEL }, 8, false, false);
		}

		ds.setName(title);
		try {
			ds.setRGBMerged(true);
		}
		catch (final IllegalArgumentException e) {
			// ignore if we cannot make it an RGB image
			log.debug(e);
		}

		return ds;
	}

	/**
	 * Returns the path to a backdrop image file. Chooses randomly from those
	 * present in the "images/about" folder off the ImageJ base directory.
	 * 
	 * @return file path of the chosen image
	 */
	private File getRandomAboutImagePath() {
		final File imagesDir =
			new File(appService.getApp().getBaseDirectory(), "images");
		final File aboutDir = new File(imagesDir, "about");
		if (!aboutDir.exists()) {
			// no "about" folder found
			log.warn("About folder '" + aboutDir.getPath() + "' does not exist.");
			return null;
		}

		// get list of available image files
		final File[] aboutFiles = aboutDir.listFiles(new java.io.FileFilter() {

			@Override
			public boolean accept(final File pathname) {
				// ignore .txt metadata files
				return !pathname.getName().toLowerCase().endsWith(".txt");
			}
		});

		// choose a random image file
		final MersenneTwisterFast rng = new MersenneTwisterFast();
		final int index = rng.nextInt(aboutFiles.length);
		return aboutFiles[index];
	}

	/**
	 * Draws the textual information over a given merged color Dataset.
	 */
	private void drawTextOverImage(final Dataset ds) {
		textChannels = new ChannelCollection(textColor);
		outlineChannels = new ChannelCollection(outlineColor);
		final DrawingTool tool = new DrawingTool(ds, rendSrv);
		tool.setUAxis(0);
		tool.setVAxis(1);
		final long width = ds.dimension(0);
		final long x = width / 2;
		long y = 50;
		tool.setTextAntialiasing(true);
		// tool.setTextOutlineWidth(5);
		tool.setFontSize(largestFontSize);
		drawOutlinedText(tool, x, y, getApp().getTitle(),
			TextJustification.CENTER, textChannels, outlineChannels);
		y += 5 * tool.getFontSize() / 4;
		tool.setFontSize((int) Math.round(0.6 * largestFontSize));
		for (final String line : getTextBlock()) {
			drawOutlinedText(tool, x, y, line, TextJustification.CENTER,
				textChannels, outlineChannels);
			y += 5 * tool.getFontSize() / 4;
		}
	}

	/**
	 * Draws a text string and outline in two different sets of fill values.
	 */
	private void drawOutlinedText(final DrawingTool tool, final long x,
		final long y, final String text, final TextJustification just,
		final ChannelCollection textValues, final ChannelCollection outlineValues)
	{
		tool.setChannels(outlineValues);
		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				if (dx == 0 && dy == 0) continue;
				tool.drawText(x + dx, y + dy, text, just);
			}
		}
		tool.setChannels(textValues);
		tool.drawText(x, y, text, just);
	}

	/**
	 * Returns the paragraph of textual information to display over the backdrop
	 * image.
	 */
	private List<String> getTextBlock() {
		final Manifest mft = getApp().getManifest();

		final LinkedList<String> stringList = new LinkedList<>();
		stringList.add("Version: " + getApp().getVersion());
		if (mft != null) {
			final String build = mft.getImplementationBuild();
			stringList.add("Build: " + (build == null || build.length() < 10 ? build : build.substring(0, 10)));
			stringList.add("Date: " + mft.getImplementationDate());
		}
		stringList.add("Open source image processing software");
		final int year = Calendar.getInstance().get(Calendar.YEAR);
		stringList.add("Copyright 2010 - " + year);
		stringList.add("http://imagej.net/Contributors");
		stringList.addAll(attributionStrings);

		return stringList;
	}

	/**
	 * Given an image file name (i.e. filename.ext) loads associated attributes
	 * from a text file (filename.ext.txt) if possible.
	 */
	private void loadAttributes(final File baseFile) {
		if (baseFile == null) return;
		final String fileName = baseFile.getAbsolutePath() + ".txt";
		final File file = new File(fileName);
		if (file.exists()) {
			final Pattern attributionPattern =
				Pattern.compile("attribution\\s+(.*)");
			final Pattern colorPattern =
				Pattern.compile("color\\s+([0-9]+)\\s+([0-9]+)\\s+([0-9]+)");
			final Pattern fontsizePattern =
				Pattern.compile("fontsize\\s+([1-9][0-9]*)");
			try {
				final FileInputStream fstream = new FileInputStream(file);
				final DataInputStream in = new DataInputStream(fstream);
				final BufferedReader br =
					new BufferedReader(new InputStreamReader(in));
				String strLine;
				// Read File Line By Line
				while ((strLine = br.readLine()) != null) {
					final Matcher attributionMatcher =
						attributionPattern.matcher(strLine);
					if (attributionMatcher.matches()) {
						attributionStrings.add(attributionMatcher.group(1).trim());
					}
					final Matcher colorMatcher = colorPattern.matcher(strLine);
					if (colorMatcher.matches()) {
						try {
							final int r = Integer.parseInt(colorMatcher.group(1));
							final int g = Integer.parseInt(colorMatcher.group(2));
							final int b = Integer.parseInt(colorMatcher.group(3));
							textColor = new ColorRGB(r, g, b);
						}
						catch (final Exception e) {
							// do nothing
						}
					}
					final Matcher fontsizeMatcher = fontsizePattern.matcher(strLine);
					if (fontsizeMatcher.matches()) {
						try {
							largestFontSize = Integer.parseInt(fontsizeMatcher.group(1));
						}
						catch (final Exception e) {
							// do nothing
						}
					}
				}
				// Close the input stream
				in.close();

			}
			catch (final Exception e) {
				// do nothing
			}
		}
	}

	private App getApp() {
		return appService.getApp();
	}

}
