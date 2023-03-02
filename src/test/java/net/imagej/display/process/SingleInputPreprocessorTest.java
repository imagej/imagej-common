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

package net.imagej.display.process;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import net.imagej.ChannelCollection;
import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.Position;
import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;
import net.imagej.display.DataView;
import net.imagej.display.DatasetView;
import net.imagej.display.ImageDisplay;
import net.imagej.display.OverlayService;
import net.imagej.overlay.LineOverlay;
import net.imagej.overlay.Overlay;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.scijava.Context;
import org.scijava.ItemIO;
import org.scijava.command.Command;
import org.scijava.command.CommandModule;
import org.scijava.command.CommandService;
import org.scijava.display.Display;
import org.scijava.display.DisplayService;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

/**
 * Tests {@link SingleInputPreprocessor} implementations.
 *
 * @author Curtis Rueden
 */
public class SingleInputPreprocessorTest {

	private Context context;
	private DatasetService datasetService;
	private DisplayService displayService;
	private OverlayService overlayService;
	private CommandService commandService;
	private Dataset dataset;
	private Overlay overlay;
	private Display<?> display;

	@Before
	public void setUp() {
		// create the context
		context = new Context();
		datasetService = context.service(DatasetService.class);
		displayService = context.service(DisplayService.class);
		overlayService = context.service(OverlayService.class);
		commandService = context.service(CommandService.class);

		// create and assign an active dataset
		final String name = "cute-little-guy";
		final long[] dims = { 10, 10 };
		final AxisType[] axisTypes = { Axes.X, Axes.Y };
		dataset = datasetService.create(dims, name, axisTypes, 8, false, false);
		display = displayService.createDisplay(dataset);
		displayService.setActiveDisplay(display);

		// create and assign an active overlay
		final ImageDisplay imageDisplay = (ImageDisplay) display;
		final double[] ptStart = { 0, 0 }, ptEnd = { dims[0], dims[1] };
		overlay = new LineOverlay(context, ptStart, ptEnd);
		final List<Overlay> overlays = Collections.singletonList(overlay);
		overlayService.addOverlays(imageDisplay, overlays);
		imageDisplay.get(1).setSelected(true); // activate the overlay
	}

	@After
	public void tearDown() {
		context.dispose();
	}

	/** Tests {@link ActiveChannelCollectionPreprocessor}. */
	@Test
	public void testSingleChannelCollection() {
		run(ChannelCollectionNeeded.class, ChannelCollection.class);
	}

	/** Tests {@link ActiveImagePreprocessor}'s handling of {@link Dataset}. */
	@Test
	public void testSingleDataset() {
		assertSame(dataset, run(DatasetNeeded.class, Dataset.class));
	}

	/** Tests {@link ActiveImagePreprocessor}'s handling of {@link DatasetView}. */
	@Test
	public void testSingleDatasetView() {
		final DatasetView dv = run(DatasetViewNeeded.class, DatasetView.class);
		assertSame(dataset, dv.getData());
	}

	/** Tests {@link ActiveImagePreprocessor}'s handling of {@link DataView}. */
	@Test
	public void testSingleDataView() {
		final DataView dv = run(DataViewNeeded.class, DataView.class);
		assertSame(dataset, dv.getData());
	}

	/** Tests {@link ActiveImagePreprocessor}'s handling of {@link ImageDisplay}. */
	@Test
	public void testSingleImageDisplay() {
		assertSame(display, run(ImageDisplayNeeded.class, ImageDisplay.class));
	}

	/** Tests {@link ActiveOverlayPreprocessor}. */
	@Test
	public void testSingleOverlay() {
		assertSame(overlay, run(OverlayNeeded.class, Overlay.class));
	}

	/** Tests {@link ActivePositionPreprocessor}. */
	@Test
	public void testSinglePosition() {
		run(PositionNeeded.class, Position.class);
	}

	// -- Helper methods --

	private <T, C extends HasASingleInput<T>> T run(final Class<C> moduleType,
		final Class<T> inputType)
	{
		try {
			final CommandModule m = commandService.run(moduleType, true).get();

			final Boolean success = (Boolean) m.getOutput("success");
			assertNotNull(success);
			assertTrue(success);

			final Object thing = m.getInput("thing");
			assertTrue(inputType.isInstance(thing));
			@SuppressWarnings("unchecked")
			final T typedThing = (T) thing;
			return typedThing;
		}
		catch (final InterruptedException exc) {
			throw assertionError("Module execution failed", exc);
		}
		catch (final ExecutionException exc) {
			throw assertionError("Module execution failed", exc);
		}
	}

	private AssertionError assertionError(final String message,
		final Throwable cause)
	{
			final AssertionError err = new AssertionError(message);
			err.initCause(cause);
			return err;
	}

	// -- Helper classes --

	@Plugin(type = Command.class)
	public static class ChannelCollectionNeeded extends
		HasASingleInput<ChannelCollection>
	{
		// No implementation needed.
	}

	@Plugin(type = Command.class)
	public static class DatasetNeeded extends HasASingleInput<Dataset> {
		// No implementation needed.
	}

	@Plugin(type = Command.class)
	public static class DatasetViewNeeded extends HasASingleInput<DatasetView> {
		// No implementation needed.
	}

	@Plugin(type = Command.class)
	public static class DataViewNeeded extends HasASingleInput<DataView> {
		// No implementation needed.
	}

	@Plugin(type = Command.class)
	public static class ImageDisplayNeeded extends HasASingleInput<ImageDisplay> {
		// No implementation needed.
	}

	@Plugin(type = Command.class)
	public static class OverlayNeeded extends HasASingleInput<Overlay> {
		// No implementation needed.
	}

	@Plugin(type = Command.class)
	public static class PositionNeeded extends HasASingleInput<Position> {
		// No implementation needed.
	}

	public static abstract class HasASingleInput<T> implements Command {

		@Parameter(persist = false)
		private T thing;

		@Parameter(required = false, persist = false)
		private int input;

		@Parameter(type = ItemIO.OUTPUT, persist = false)
		private boolean success;

		@Override
		public void run() {
			success = true;
		}
	}

}
