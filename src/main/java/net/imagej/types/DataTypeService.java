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

package net.imagej.types;

import java.util.List;

import net.imagej.ImageJService;
import net.imglib2.type.numeric.NumericType;

import org.scijava.plugin.SingletonService;

/**
 * Service interface that supports runtime discovery of and conversion between
 * {@link DataType}s.
 * 
 * @author Barry DeZonia
 */
public interface DataTypeService extends SingletonService<DataType<?>>,
	ImageJService
{

	/**
	 * Returns a list of all DataTypes known to system. List is in order sorted by
	 * DataType's internal name.
	 */
	// Override to update Javadoc
	@Override
	List<DataType<?>> getInstances();

	/**
	 * Returns the DataType whose internal name matches the given typeName.
	 * Returns null if not found.
	 */
	DataType<?> getTypeByName(String typeName);

	/**
	 * Returns the DataType whose internal type class matches the given typeClass.
	 * Returns null if not found.
	 */
	DataType<?> getTypeByClass(Class<?> typeClass);

	/**
	 * Returns the DataType whose internal type class matches the given
	 * specifications exactly. If a type is unbounded it is expected that bitCount
	 * will be specified as -1.
	 */
	DataType<?> getTypeByAttributes(int bitCount, boolean bounded, boolean complex,
		boolean floating, boolean signed);

	/**
	 * Fills an output with a cast from an input given information about their
	 * DataTypes. This version of cast() can throw IllegalArgumentException if it
	 * can't find a safe cast. Use the alternate version of cast() that takes a
	 * temporary working variable for fully safe casting.
	 * 
	 * @param inputType The DataType of the input.
	 * @param input The input variable to cast from.
	 * @param outputType The DataType of the output
	 * @param output The output variable to cast into.
	 */
	<U extends NumericType<U>, V extends NumericType<V>> void cast(
		DataType<U> inputType, U input, DataType<V> outputType, V output);

	/**
	 * Fills an output with a cast from an input given information about their
	 * DataTypes. This version always succeeds. It requires a temporary working
	 * variable of type BigComplex to be passed in.
	 * 
	 * @param inputType The DataType of the input.
	 * @param input The input variable to cast from.
	 * @param outputType The DataType of the output
	 * @param output The output variable to cast into.
	 * @param tmp The working variable the method may use internally.
	 */
	<U extends NumericType<U>, V extends NumericType<V>> void cast(
		DataType<U> inputType, U input, DataType<V> outputType, V output,
		BigComplex tmp);
}
