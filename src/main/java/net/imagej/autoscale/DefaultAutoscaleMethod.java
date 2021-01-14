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

package net.imagej.autoscale;

import java.util.List;

import net.imagej.minmax.MinMaxMethod;
import net.imglib2.IterableInterval;
import net.imglib2.type.numeric.RealType;

import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginService;

/**
 * Computes a data range from the entire set of values in an
 * {@link IterableInterval}.
 * 
 * @author Barry DeZonia
 */
@Plugin(type = AutoscaleMethod.class, name = "Default")
public class DefaultAutoscaleMethod<T extends RealType<T>> extends
	AbstractAutoscaleMethod<T>
{

	@Parameter
	private PluginService pluginService;

	@Override
	public DataRange getRange(final IterableInterval<T> interval)
	{
		@SuppressWarnings("rawtypes")
		final List<MinMaxMethod> methods =
			pluginService.createInstancesOfType(MinMaxMethod.class);
    @SuppressWarnings("unchecked")
		final MinMaxMethod<T> minmax = methods.get(0);
		minmax.initialize(interval);
		minmax.process();
		double min = minmax.getMin().getRealDouble();
		double max = minmax.getMax().getRealDouble();

		// NB - never return a display range of zero
		if (min == max) {
			final T theType = interval.firstElement();
			min = theType.getMinValue();
			max = theType.getMaxValue();
		}

		return new DataRange(min, max);
	}

}
