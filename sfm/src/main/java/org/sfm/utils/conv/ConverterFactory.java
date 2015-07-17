package org.sfm.utils.conv;

import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.jdbc.impl.getter.ResultSetGetterFactory;
import org.sfm.map.impl.FieldMapperColumnDefinition;
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

	private static final Map<Class<? extends Number>, Converter<? extends Number, ? extends Number>> numberConverters =
			new HashMap<Class<? extends Number>, Converter<? extends Number, ? extends Number>>();
	static {
		numberConverters.put(Byte.class, new Converter<Number, Byte>() {
			@Override
			public Byte convert(Number in) {
				return in.byteValue();
			}
		});
		numberConverters.put(Short.class, new Converter<Number, Short>() {
			@Override
			public Short convert(Number in) {
				return in.shortValue();
			}
		});
		numberConverters.put(Integer.class, new Converter<Number, Integer>() {
			@Override
			public Integer convert(Number in) {
				return in.intValue();
			}
		});
		numberConverters.put(Long.class, new Converter<Number, Long>() {
			@Override
			public Long convert(Number in) {
				return in.longValue();
			}
		});
		numberConverters.put(Float.class, new Converter<Number, Float>() {
			@Override
			public Float convert(Number in) {
				return in.floatValue();
			}
		});
		numberConverters.put(Double.class, new Converter<Number, Double>() {
			@Override
			public Double convert(Number in) {
				return in.doubleValue();
			}
		});
		numberConverters.put(BigDecimal.class, new Converter<Number, BigDecimal>() {
			@Override
			public BigDecimal convert(Number in) {
				return new BigDecimal(in.doubleValue());
			}
		});
		numberConverters.put(BigInteger.class, new Converter<Number, BigInteger>() {
			@Override
			public BigInteger convert(Number in) { return new BigInteger(String.valueOf(in));}
		});
	}

	private static final Map<Class<?>, Converter<CharSequence, ?>> charSequenceConverters  = new HashMap<Class<?>, Converter<CharSequence, ?>>();
	static {
		charSequenceConverters.put(String.class, new Converter<CharSequence, String>() {
			@Override
			public String convert(CharSequence in) throws Exception {
				return in.toString();
			}
		});
		charSequenceConverters.put(Byte.class, new Converter<CharSequence, Byte>() {
			@Override
			public Byte convert(CharSequence in) throws Exception {
				return Byte.valueOf(in.toString());
			}
		});
		charSequenceConverters.put(Character.class, new Converter<CharSequence, Character>() {
			@Override
			public Character convert(CharSequence in) throws Exception {
				return Character.valueOf((char) Integer.parseInt(in.toString()));
			}
		});
		charSequenceConverters.put(Short.class, new Converter<CharSequence, Short>() {
			@Override
			public Short convert(CharSequence in) throws Exception {
				return Short.valueOf(in.toString());
			}
		});
		charSequenceConverters.put(Integer.class, new Converter<CharSequence, Integer>() {
			@Override
			public Integer convert(CharSequence in) throws Exception {
				return Integer.valueOf(in.toString());
			}
		});

		charSequenceConverters.put(Long.class, new Converter<CharSequence, Long>() {
			@Override
			public Long convert(CharSequence in) throws Exception {
				return Long.valueOf(in.toString());
			}
		});
		charSequenceConverters.put(Float.class, new Converter<CharSequence, Float>() {
			@Override
			public Float convert(CharSequence in) throws Exception {
				return Float.valueOf(in.toString());
			}
		});
		charSequenceConverters.put(Double.class, new Converter<CharSequence, Double>() {
			@Override
			public Double convert(CharSequence in) throws Exception {
				return Double.valueOf(in.toString());
			}
		});
	}

	public static <P, F> Converter<F, P> getConverter(Class<F> inType, Class<P> outType) {
		return getConverter(inType, (Type)outType);
	}
	@SuppressWarnings("unchecked")
	public static <P, F> Converter<F, P> getConverter(Class<F> inType, Type outType) {
		if (outType.equals(String.class)) {
			return (Converter<F, P>) new ToStringConverter<F>();
		} else if (TypeHelper.isNumber(outType) && TypeHelper.isNumber(inType)) {
			return (Converter<F, P>) numberConverters.get(TypeHelper.wrap(outType));
		} else if (TypeHelper.isClass(outType, URL.class)) {
			return  (Converter<F, P>)new StringToURLConverter<F>();
		} else if (TypeHelper.isAssignable(CharSequence.class, inType)) {
			if (TypeHelper.isAssignable(Enum.class, outType)) {
				return new CharSequenceToEnumConverter(TypeHelper.toClass(outType));
			} else {
				return (Converter<F, P>) charSequenceConverters.get(TypeHelper.toClass(outType));
			}
		}
		return null;
	}

	static class CharSequenceToEnumConverter<E extends Enum<E>> implements Converter<CharSequence, E> {
		private final Class<E> enumClass;

		CharSequenceToEnumConverter(Class<E> enumClass) {
			this.enumClass = enumClass;
		}

		@Override
		public E convert(CharSequence in) throws Exception {
			return Enum.valueOf(enumClass, in.toString());
		}
	}
}
