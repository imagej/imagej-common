
package net.imagej.convert;

import net.imglib2.type.BooleanType;
import net.imglib2.type.logic.BoolType;
import org.scijava.Priority;
import org.scijava.convert.AbstractConverter;
import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;

/**
 * Converts {@link Boolean}s into {@link BooleanType}s
 *
 * @author Gabriel Selzer
 */
@SuppressWarnings("rawtypes")
@Plugin(type = Converter.class, priority = Priority.VERY_LOW)
public class BooleanToBooleanTypeConverter extends
	AbstractConverter<Boolean, BooleanType>
{

	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(Object o, Class<T> aClass) {
		if (!aClass.equals(BooleanType.class)) throw new IllegalArgumentException(
			aClass + " is not BooleanType.class");
		if (!(o instanceof Boolean)) throw new IllegalArgumentException(o +
			" is not a Boolean");
		return (T) new BoolType((Boolean) o);
	}

	@Override
	public Class<BooleanType> getOutputType() {
		return BooleanType.class;
	}

	@Override
	public Class<Boolean> getInputType() {
		return Boolean.class;
	}
}
