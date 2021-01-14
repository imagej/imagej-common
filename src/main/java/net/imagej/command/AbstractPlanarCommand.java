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

package net.imagej.command;

import net.imagej.display.DatasetView;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.view.Views;

import org.scijava.plugin.Parameter;

/**
 * Abstract superclass for commands that can operate on a single plane.
 * 
 * @author Mark Hiner
 */
public abstract class AbstractPlanarCommand implements PlanarCommand {
  
  // -- Parameters --
  
  @Parameter
  private DatasetView view;
  
  @Parameter
	private boolean doWholeDataset;

  // -- Runnable API Methods --
  
  @Override
  public void run() {
    RandomAccessibleInterval<?> img = getView().getData().getImgPlus();

    if (isPlanar()) {
      
      // assume x,y are first
      for (int i=2; i<img.numDimensions(); i++) {
        img = Views.hyperSlice(img, i, getView().getLongPosition(i));
      }
    }
    run(img);
  }
  
  // -- PlanarCommand API methods --

  @Override
  public DatasetView getView() {
    return view;
  }
  
  @Override
	public boolean isPlanar() {
   return !doWholeDataset; 
  }
  
  @Override
  public void setView(DatasetView view) {
    this.view = view;
  }
  
  @Override
	public void setPlanar(boolean planar) {
    doWholeDataset = !planar;
  }
}
