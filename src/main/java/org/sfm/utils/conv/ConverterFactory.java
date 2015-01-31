package org.sfm.utils.conv;

import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.jdbc.impl.getter.ResultSetGetterFactory;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConverterFactory {

	@SuppressWarnings("serial")
	private static final Map<Class<? extends Number>, Converter<? extends Number, ? extends Number>> numberConverters =
			new HashMap<Class<? extends Number>, Converter<? extends Number, ? extends Number>>() {{
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
	public static <P, F> Converter<F, P> getConverter(Class<F> inType, Type outType) {
		if (outType.equals(String.class)) {
			return (Converter<F, P>) new ToStringConverter<F>();
		} else if (TypeHelper.isNumber(outType) && TypeHelper.isNumber(inType)) {
			return (Converter<F, P>) numberConverters.get(TypeHelper.wrap(outType));
		} else if (TypeHelper.isClass(outType, URL.class)) {
			return  (Converter<F, P>)new StringToURLConverter<F>();
		}  else if (TypeHelper.isArray(outType)) {
			return  newArrayConverter(TypeHelper.getComponentType(outType));
		} else if (TypeHelper.isAssignable(List.class, outType)) {
			return  newArrayToListConverter(TypeHelper.getComponentType(outType));
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static <F, P, E> Converter<F, P> newArrayConverter(Type eltType) {
		Getter<ResultSet, E> elementGetter = new ResultSetGetterFactory().newGetter(eltType, new JdbcColumnKey("elt", 2));
		if (elementGetter == null) return null;
		return (Converter<F, P>)new ArrayConverter<E>((Class<E>) TypeHelper.toClass(eltType), elementGetter);
	}
	@SuppressWarnings("unchecked")
	private static <F, P, E> Converter<F, P> newArrayToListConverter(Type eltType) {
		Getter<ResultSet, E> elementGetter = new ResultSetGetterFactory().newGetter(eltType, new JdbcColumnKey("elt", 2));
		if (elementGetter == null) return null;
		return (Converter<F, P>)new ArrayToListConverter<E>(elementGetter);
	}
	
}
