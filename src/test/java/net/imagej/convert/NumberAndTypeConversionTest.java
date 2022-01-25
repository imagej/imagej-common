package net.imagej.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.integer.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.scijava.Context;
import org.scijava.convert.ConvertService;

import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.type.operators.ValueEquals;

@RunWith(Parameterized.class)
public class NumberAndTypeConversionTest {
	
	@Parameters(name="Type conversion test {index}: {0} <=> {1}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{new Double(Double.POSITIVE_INFINITY), new DoubleType(Double.POSITIVE_INFINITY)},
				{new Float(Float.NEGATIVE_INFINITY), new FloatType(Float.NEGATIVE_INFINITY)},
				{new Integer(Integer.MAX_VALUE), new IntType(Integer.MAX_VALUE)},
				{new Long(Long.MAX_VALUE), new LongType(Long.MAX_VALUE)},
				{new Integer(255), new UnsignedByteType(255)},
				// Do one for each RealType
				{new Byte((byte) 0), new ByteType((byte) 0)},
				{new Integer(0), new IntType(0)},
				{new Long(0), new LongType(0l)},
				{new Short((short) 0), new ShortType((short) 0)},
				{new Double(0.), new DoubleType(0.)},
				{new Float(0.), new FloatType(0f)}
			});
	}

	@Parameter(0)
	public Object number;

	@Parameter(1)
	public Object numericType;

	private ConvertService convertService;

	@Before
	public void setUp() {
		final Context ctx = new Context();
		convertService = ctx.getService(ConvertService.class);
	}

	@After
	public void tearDown() {
		convertService.getContext().dispose();
		convertService = null;
	}

	/**
	 * Test conversion from {@link Number} to {@link NumericType}
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testNumberToNumericType() {
		assertTrue("Conversion support from number to numeric type", convertService.supports(number, numericType.getClass()));
		ValueEquals converted = (ValueEquals) convertService.convert(number, numericType.getClass());
		assertTrue("Converted type value equality", converted.valueEquals(numericType));
	}

	/**
	 * Test conversion from {@link Number} to {@link NumericType}
	 */
	@Test
	public void testNumericTypeToNumber() {
		assertTrue("Conversion support from numeric type to number", convertService.supports(numericType, number.getClass()));
		Number converted = (Number) convertService.convert(numericType, number.getClass());
		assertEquals("Converted value equality", number, converted);
	}
}
