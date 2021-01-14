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

package net.imagej;

import net.imglib2.type.Type;
import net.imglib2.type.numeric.ComplexType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.RealType;

/**
 * ImgPlusService is an {@link ImageJService} that allows one to cast weakly
 * typed {@link ImgPlus}es to more strongly typed versions.
 * 
 * @author Barry DeZonia
 */
public interface ImgPlusService extends ImageJService {

	/**
	 * Given an ImgPlus of unknown type this returns a typed ImgPlus of a given
	 * type. Returns null if the given ImgPlus does not have data of the given
	 * type.
	 */
	public <T extends Type<T>> ImgPlus<T> asType(ImgPlus<?> ip, T type);

	/**
	 * Given an ImgPlus of unknown type this returns a typed ImgPlus of type Type.
	 * Returns null if the given ImgPlus does not have data derived from Type.
	 */
	public ImgPlus<Type<?>> typed(ImgPlus<?> ip);

	/**
	 * Given an ImgPlus of unknown type this returns a typed ImgPlus of type
	 * NumericType. Returns null if the given ImgPlus does not have data derived
	 * from NumericType.
	 */
	public ImgPlus<NumericType<?>> numeric(ImgPlus<?> ip);

	/**
	 * Given an ImgPlus of unknown type this returns a typed ImgPlus of type
	 * ComplexType. Returns null if the given ImgPlus does not have data derived
	 * from ComplexType.
	 */
	public ImgPlus<ComplexType<?>> complex(ImgPlus<?> ip);

	/**
	 * Given an ImgPlus of unknown type this returns a typed ImgPlus of type
	 * RealType. Returns null if the given ImgPlus does not have data derived from
	 * RealType.
	 */
	public ImgPlus<RealType<?>> real(ImgPlus<?> ip);

	/**
	 * Given an ImgPlus of unknown type this returns a typed ImgPlus of type
	 * IntegerType. Returns null if the given ImgPlus does not have data derived
	 * from IntegerType.
	 */
	public ImgPlus<IntegerType<?>> integer(ImgPlus<?> ip);

	// TODO - once FloatingType is an Imglib type

//	/**
//	 * Given an ImgPlus of unknown type this returns a typed ImgPlus of type
//	 * FloatingType. Returns null if the given ImgPlus does not have data derived
//	 * from FloatingType.
//	 */
// 	public ImgPlus<FloatingType<?>> floating(ImgPlus<?> ip);

}
