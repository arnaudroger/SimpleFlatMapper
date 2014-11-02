package org.sfm.utils.conv;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.sfm.reflect.TypeHelper;

public class ConverterFactory {

	@SuppressWarnings("serial")
	private static Map<Class<? extends Number>, Converter<? extends Number, ? extends Number>> numberConvertors = new HashMap<Class<? extends Number>, Converter<? extends Number, ? extends Number>>() {{
		put(Byte.class, new Converter<Number, Byte>() {
			@Override
			public Byte convert(Number in) { return new Byte(in.byteValue());}
		});
		put(Short.class, new Converter<Number, Short>() {
			@Override
			public Short convert(Number in) { return new Short(in.shortValue());}
		});
		put(Integer.class, new Converter<Number, Integer>() {
			@Override
			public Integer convert(Number in) { return new Integer(in.intValue());}
		});
		put(Long.class, new Converter<Number, Long>() {
			@Override
			public Long convert(Number in) { return new Long(in.longValue());}
		});
		put(Float.class, new Converter<Number, Float>() {
			@Override
			public Float convert(Number in) { return new Float(in.floatValue());}
		});
		put(Double.class, new Converter<Number, Double>() {
			@Override
			public Double convert(Number in) { return new Double(in.doubleValue());}
		});
		put(BigDecimal.class, new Converter<Number, BigDecimal>() {
			@Override
			public BigDecimal convert(Number in) { return new BigDecimal(in.doubleValue());}
		});
		put(BigInteger.class, new Converter<Number, BigInteger>() {
			@Override
			public BigInteger convert(Number in) { return new BigInteger(String.valueOf(in));}
		});
	}};
	
	@SuppressWarnings("unchecked")
	public static <P, F> Converter<F, P> getConverter(Class<F> inType, Class<P> outType) {
		if (outType.equals(String.class)) {
			return (Converter<F, P>) new ToStringConverter<F>();
		} else if (TypeHelper.isNumber(outType) && TypeHelper.isNumber(inType)) {
			return (Converter<F, P>) numberConvertors.get(TypeHelper.wrap(outType));
		} else if (URL.class.equals(outType)) {
			return  (Converter<F, P>)new URLConvertor<F>();
		}
		throw new IllegalArgumentException("No converter from " + inType + " to  " + outType);
	}
	
}
