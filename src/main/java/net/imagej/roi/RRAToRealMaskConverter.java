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

package net.imagej.roi;

import net.imglib2.RealRandomAccessible;
import net.imglib2.roi.Masks;
import net.imglib2.roi.RealMask;
import net.imglib2.type.logic.BoolType;

import org.scijava.Priority;
import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;

/**
 * Converts a {@code RealRandomAccessible<BoolType>} to {@code RealMask}.
 *
 * @author Alison Walter
 */
@Plugin(type = Converter.class, priority = Priority.LOW)
public class RRAToRealMaskConverter extends
	AbstractRRAToRealMaskConverter<RealRandomAccessible<BoolType>, RealMask>
{

	@Override
	public Class<RealMask> getOutputType() {
		return RealMask.class;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class<RealRandomAccessible<BoolType>> getInputType() {
		return (Class) RealRandomAccessible.class;
	}

	@Override
	public RealMask convert(final RealRandomAccessible<BoolType> src) {
		return Masks.toRealMask(src);
	}

}
