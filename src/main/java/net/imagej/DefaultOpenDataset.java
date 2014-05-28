/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2014 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
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
package net.imagej;

import io.scif.config.SCIFIOConfig;
import io.scif.img.ImageRegion;
import io.scif.img.Range;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.imglib2.meta.Axes;
import net.imglib2.meta.AxisType;

import org.scijava.ItemIO;
import org.scijava.ItemVisibility;
import org.scijava.command.ContextCommand;
import org.scijava.log.LogService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Default {@link OpenDataset} implementation. Provides configuration
 * options for the import process.
 * 
 * @author Mark Hiner
 */
@Plugin(type = OpenDataset.class, menuPath = "File > Import > Image",
	label = "Select import options...")
public class DefaultOpenDataset extends ContextCommand implements OpenDataset {

	private static final int MAX_HEADER = 55;

	@Parameter
	private DatasetService datasetService;

	@Parameter
	private LogService logService;

	@Parameter(visibility = ItemVisibility.MESSAGE, persist = false,
		initializer = "setHeader")
	private String header;

	@Parameter(required = false)
	private Boolean crop;

	//TODO callback to enable/disable these fields based on crop value
	@Parameter(required = false, autoFill = false, min = "0")
	private Integer x;

	@Parameter(required = false, min = "0")
	private Integer y;

	@Parameter(required = false, min = "0")
	private Integer w;

	@Parameter(required = false, min = "0")
	private Integer h;

	@Parameter(required = false, label = "Image indices")
	private String range;

	@Parameter(required = false, label = "Group similar files")
	private Boolean groupFiles;

	@Parameter(type = ItemIO.INPUT)
	private String source;

	@Parameter(type = ItemIO.OUTPUT)
	private Dataset dataset;

	@Parameter(type = ItemIO.OUTPUT)
	private IOException error;

	@Override
	public void run() {
		final SCIFIOConfig config = new SCIFIOConfig();

		// Set the image index range if desired
		if (range != null && !range.isEmpty()) {
			try {
				config.imgOpenerSetRange(range);
			} catch (IllegalArgumentException e) {
				logService.warn("Ignoring bad range: " + range);
			}
		}

		// Crop if desired
		if (crop != null && crop) {
			if (validRange(x, y, w, h)) {
				Map<AxisType, Range> region = new HashMap<AxisType, Range>();
				region.put(Axes.X, new Range(new Long(x), new Long(w)));
				region.put(Axes.Y, new Range(new Long(y), new Long(h)));
				config.imgOpenerSetRegion(new ImageRegion(region));
			}
			else {
				logService.warn("ignoring bad crop region: " + x + ", " + y + ", " + w +
					", " + h);
			}
		}

		// Set the groupFiles flag if desired
		if (groupFiles != null) {
			config.groupableSetGroupFiles(groupFiles);
		}

		// Open the dataset
		try {
			dataset = datasetService.open(source, config);
		}
		catch (IOException e) {
			error = e;
			logService.error(e);
		}
	}

	/**
	 * @return true if all params are non-null and positive.
	 */
	private boolean validRange(final Integer x, final Integer y, final Integer w,
		final Integer h)
	{
		return (x != null && y != null && w != null && h != null) &&
			(x >= 0 && y >= 0 && w >= 0 && h >= 0);
	}

	// Callback method
	@SuppressWarnings("unused")
	private void setHeader() {
		if (source != null) {
			// Truncate long headers if needed
			if (source.length() > MAX_HEADER) {
				header = "..." + source.substring(source.length() - (MAX_HEADER - 3));
			}
			else {
				header = source;
			}
		}
	}
}
