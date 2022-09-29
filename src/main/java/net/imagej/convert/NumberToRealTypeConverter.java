
package net.imagej.convert;

import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import org.scijava.Priority;
import org.scijava.convert.AbstractConverter;
import org.scijava.convert.Converter;
import org.scijava.plugin.Plugin;
import org.scijava.util.Types;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts {@link Number}s into {@link RealType}s, or to the superinterfaces of
 * {@link RealType}. NB Conversions from {@link Number} into
 * <b>implementations</b> of {@link RealType} are handled by
 * {@link org.scijava.convert.DefaultConverter}.
 *
 * @author Gabriel Selzer
 */
@SuppressWarnings("rawtypes")
@Plugin(type = Converter.class, priority = Priority.VERY_LOW)
public class NumberToRealTypeConverter extends
	AbstractConverter<Number, RealType>
{

	// Mapping of each Number into their corresponding RealType
	private static final Map<Class<? extends Number>, Class<?>> NUMBER_TYPES =
		new HashMap<>();
	static {
		NUMBER_TYPES.put(Byte.class, ByteType.class);
		NUMBER_TYPES.put(Short.class, ShortType.class);
		NUMBER_TYPES.put(Integer.class, IntType.class);
		NUMBER_TYPES.put(Long.class, LongType.class);
		NUMBER_TYPES.put(Float.class, FloatType.class);
		NUMBER_TYPES.put(Double.class, DoubleType.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(Object o, Class<T> aClass) {
		if (!NUMBER_TYPES.containsKey(o.getClass())) {
			throw new IllegalArgumentException("Object " + o + " is not a Number!");
		}
		Class<?> srcType = Types.unbox(o.getClass());
		Class<?> destType = NUMBER_TYPES.get(o.getClass());
		try {
			return (T) destType.getConstructor(srcType).newInstance(o);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Object " + o +
				"cannot be passed to the constructor of " + destType);
		}
	}

	@Override
	public Class<RealType> getOutputType() {
		return RealType.class;
	}

	@Override
	public Class<Number> getInputType() {
		return Number.class;
	}
}
