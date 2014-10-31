package org.sfm.jdbc.jooq;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.jooq.Record;
import org.sfm.jdbc.jooq.conv.ToStringConverter;
import org.sfm.map.GetterFactory;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;
import org.sfm.utils.Converter;

public class RecordGetterFactory<R extends Record> implements
		GetterFactory<R, JooqFieldKey> {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <P> Getter<R, P> newGetter(Type genericType, JooqFieldKey key) {
		Class<Object> propretyClass = TypeHelper.toClass(genericType);
		if (Enum.class.isAssignableFrom(propretyClass)) {
			Class<?> columnType = key.getField().getType();
			
			if (Number.class.isAssignableFrom(columnType)) {
				return new EnumRecordOrdinalGetter(key, propretyClass);
			} else if (String.class.equals(columnType)){
				return new EnumRecordNamedGetter(key, propretyClass);
			} else {
				return null;
			}
 			
		}
		Class<P> clazz = TypeHelper.toClass(genericType);
		
		if (areCompatible(clazz, key.getField().getType())) {
			return new RecordGetter<R, P>(key.getIndex());
		} else {
			return newRecordGetterWithConverter(key.getField().getType(), clazz, key.getIndex());
		}
	}
	
	private boolean areCompatible(Class<?> target, Class<?> source) {
		Class<?> wrapTarget = wrap(target);
		Class<?> wrapSource = wrap(source);
		return wrapTarget.isAssignableFrom(wrapSource);
	}
	
	private Class<?> wrap(Class<?> target) {
		if (!target.isPrimitive()) {
			return target;
		}
		return wrappers.get(target);
	}
	
	@SuppressWarnings("serial")
	static Map<Class<?>, Class<?>> wrappers = new HashMap<Class<?>, Class<?>>() {{
		put(boolean.class, Boolean.class);
		put(byte.class, Byte.class);
		put(short.class, Short.class);
		put(char.class, Character.class);
		put(int.class, Integer.class);
		put(long.class, Long.class);
		put(float.class, Float.class);
		put(double.class, Double.class);
	}};

	private <P, F> Getter<R, P> newRecordGetterWithConverter(Class<F> inType, Class<P> outType, int index) {
		Converter<F, P> converter = getConverter(inType, outType);
		return new RecordGetterWithConverter<R, P, F>(index, converter);
	}

	@SuppressWarnings("serial")
	static Map<Class<? extends Number>, Converter<? extends Number, ? extends Number>> numberConvertors = new HashMap<Class<? extends Number>, Converter<? extends Number, ? extends Number>>() {{
		put(Byte.class, new Converter<Number, Byte>() {
			@Override
			public Byte convert(Number in) { return new Byte(in.byteValue());}
		});
		put(byte.class, new Converter<Number, Byte>() {
			@Override
			public Byte convert(Number in) { return new Byte(in.byteValue());}
		});
		put(Short.class, new Converter<Number, Short>() {
			@Override
			public Short convert(Number in) { return new Short(in.shortValue());}
		});
		put(short.class, new Converter<Number, Short>() {
			@Override
			public Short convert(Number in) { return new Short(in.shortValue());}
		});
		put(Integer.class, new Converter<Number, Integer>() {
			@Override
			public Integer convert(Number in) { return new Integer(in.intValue());}
		});
		put(int.class, new Converter<Number, Integer>() {
			@Override
			public Integer convert(Number in) { return new Integer(in.intValue());}
		});
		put(Long.class, new Converter<Number, Long>() {
			@Override
			public Long convert(Number in) { return new Long(in.longValue());}
		});
		put(long.class, new Converter<Number, Long>() {
			@Override
			public Long convert(Number in) { return new Long(in.longValue());}
		});
		put(Float.class, new Converter<Number, Float>() {
			@Override
			public Float convert(Number in) { return new Float(in.floatValue());}
		});
		put(float.class, new Converter<Number, Float>() {
			@Override
			public Float convert(Number in) { return new Float(in.floatValue());}
		});
		put(Double.class, new Converter<Number, Double>() {
			@Override
			public Double convert(Number in) { return new Double(in.doubleValue());}
		});
		put(double.class, new Converter<Number, Double>() {
			@Override
			public Double convert(Number in) { return new Double(in.doubleValue());}
		});
		
	}};
	@SuppressWarnings("unchecked")
	private <P, F> Converter<F, P> getConverter(Class<F> inType, Class<P> outType) {
		if (outType.equals(String.class)) {
			return (Converter<F, P>) new ToStringConverter<F>();
		} else if (isNumber(outType) && isNumber(inType)) {
			return (Converter<F, P>) numberConvertors.get(outType);
		}
		throw new IllegalArgumentException("No converter from " + inType + " to  " + outType);
	}
	
	private boolean isNumber(Class<?> clazz) {
		return numberConvertors.containsKey(clazz);
	}
}
