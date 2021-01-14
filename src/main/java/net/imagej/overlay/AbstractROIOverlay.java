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

package net.imagej.overlay;

import net.imglib2.roi.RegionOfInterest;

import org.scijava.Context;

/**
 * An overlay that has an associated region of interest
 * 
 * @author Lee Kamentsky
 */
public abstract class AbstractROIOverlay<R extends RegionOfInterest> extends
	AbstractOverlay
{

	private static final long serialVersionUID = 1L;

	private R roi;

	// default constructor for use by serialization code
	//   (see AbstractOverlay::duplicate())
	protected AbstractROIOverlay(R roi) {
		super(roi);
		this.roi = roi;
	}
	
	protected AbstractROIOverlay(final Context context, final R roi) {
		super(context, roi);
		this.roi = roi;
	}

	// TODO - Have this class implement ROIOverlay which defines
	// getRegionOfInterest(), rather than having the base Overlay interface have
	// that method. This avoids confusion with non-ROI Overlay implementation (so
	// no getRegionOfInterest() method returning null for them).

	@Override
	public R getRegionOfInterest() {
		return roi;
	}

	protected void setRegionOfInterest(R roi) {
		this.roi = roi;
	}

	/*
	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeObject(roi);
	}

	@Override
	public void readExternal(final ObjectInput in) throws IOException,
		ClassNotFoundException
	{
		super.readExternal(in);
		roi = (R) in.readObject();
	}
	*/
}
