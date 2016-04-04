package org.sfm.utils.conv;

import org.sfm.map.impl.JodaTimeClasses;
import org.sfm.reflect.TypeHelper;
import org.sfm.utils.conv.joda.JodaTimeConverterFactory;

/*IFJAVA8_START
import org.sfm.utils.conv.time.JavaTimeConverterFactory;
import java.time.temporal.Temporal;
IFJAVA8_END*/

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConverterFactory {

	private static final Map<Class<? extends Number>, Converter<? extends Number, ? extends Number>> numberConverters =
			new HashMap<Class<? extends Number>, Converter<? extends Number, ? extends Number>>();
	static {
		numberConverters.put(Byte.class, new Converter<Number, Byte>() {
			@Override
			public Byte convert(Number in) {
				if (in == null) return null;
				return in.byteValue();
			}
			public String toString() { return "NumberToByte"; }
		});
		numberConverters.put(Short.class, new Converter<Number, Short>() {
			@Override
			public Short convert(Number in) {
				if (in == null) return null;
				return in.shortValue();
			}
			public String toString() { return "NumberToShort"; }
		});
		numberConverters.put(Integer.class, new Converter<Number, Integer>() {
			@Override
			public Integer convert(Number in) {
				if (in == null) return null;
				return in.intValue();
			}
			public String toString() { return "NumberToInteger"; }
		});
		numberConverters.put(Long.class, new Converter<Number, Long>() {
			@Override
			public Long convert(Number in) {
				if (in == null) return null;
				return in.longValue();
			}
			public String toString() { return "NumberToLong"; }
		});
		numberConverters.put(Float.class, new Converter<Number, Float>() {
			@Override
			public Float convert(Number in) {
				if (in == null) return null;
				return in.floatValue();
			}
			public String toString() { return "NumberToFloat"; }
		});
		numberConverters.put(Double.class, new Converter<Number, Double>() {
			@Override
			public Double convert(Number in) {
				if (in == null) return null;
				return in.doubleValue();
			}
			public String toString() { return "NumberToDouble"; }
		});
		numberConverters.put(BigDecimal.class, new Converter<Number, BigDecimal>() {
			@Override
			public BigDecimal convert(Number in) {
				if (in == null) return null;
				return new BigDecimal(in.doubleValue());
			}
			public String toString() { return "NumberToBigDecimal"; }
		});
		numberConverters.put(BigInteger.class, new Converter<Number, BigInteger>() {
			@Override
			public BigInteger convert(Number in) {
				if (in == null) return null;
				return new BigInteger(String.valueOf(in));
			}
			public String toString() { return "NumberToBigInteger"; }
		});
	}

	private static final Map<Class<?>, Converter<CharSequence, ?>> charSequenceConverters  = new HashMap<Class<?>, Converter<CharSequence, ?>>();
	static {
		charSequenceConverters.put(Byte.class, new Converter<CharSequence, Byte>() {
			@Override
			public Byte convert(CharSequence in) throws Exception {
				if (in == null) return null;
				return Byte.valueOf(in.toString());
			}
			public String toString() { return "CharSequenceToByte"; }
		});
		charSequenceConverters.put(Character.class, new Converter<CharSequence, Character>() {
			@Override
			public Character convert(CharSequence in) throws Exception {
				if (in == null) return null;
				return Character.valueOf((char) Integer.parseInt(in.toString()));
			}
			public String toString() { return "CharSequenceToCharacter"; }
		});
		charSequenceConverters.put(Short.class, new Converter<CharSequence, Short>() {
			@Override
			public Short convert(CharSequence in) throws Exception {
				if (in == null) return null;
				return Short.valueOf(in.toString());
			}
			public String toString() { return "CharSequenceToShort"; }
		});
		charSequenceConverters.put(Integer.class, new Converter<CharSequence, Integer>() {
			@Override
			public Integer convert(CharSequence in) throws Exception {
				if (in == null) return null;
				return Integer.valueOf(in.toString());
			}
			public String toString() { return "CharSequenceToInteger"; }
		});

		charSequenceConverters.put(Long.class, new Converter<CharSequence, Long>() {
			@Override
			public Long convert(CharSequence in) throws Exception {
				if (in == null) return null;
				return Long.valueOf(in.toString());
			}
			public String toString() { return "CharSequenceToLong"; }
		});
		charSequenceConverters.put(Float.class, new Converter<CharSequence, Float>() {
			@Override
			public Float convert(CharSequence in) throws Exception {
				if (in == null) return null;
				return Float.valueOf(in.toString());
			}
			public String toString() { return "CharSequenceToFloat"; }
		});
		charSequenceConverters.put(Double.class, new Converter<CharSequence, Double>() {
			@Override
			public Double convert(CharSequence in) throws Exception {
				if (in == null) return null;
				return Double.valueOf(in.toString());
			}
			public String toString() { return "CharSequenceToDouble"; }
		});
		charSequenceConverters.put(UUID.class, new Converter<CharSequence, UUID>() {
			@Override
			public UUID convert(CharSequence in) throws Exception {
				if (in == null) return null;
				return UUID.fromString(in.toString());
			}
			public String toString() { return "CharSequenceToUUID"; }
		});
	}

	public static <P, F> Converter<F, P> getConverter(Class<F> inType, Class<P> outType, Object... params) {
		return getConverter(inType, (Type)outType, params);
	}
	@SuppressWarnings("unchecked")
	public static <P, F> Converter<F, P> getConverter(Class<F> inType, Type outType, Object... params) {
		if (TypeHelper.areEquals(outType, inType)) {
			return (Converter<F, P>) new IdentityConverter<F>();
		} else if (outType.equals(String.class)) {
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
		} else if (JodaTimeClasses.isJoda(inType)) {
			return JodaTimeConverterFactory.getConverterFrom(inType, outType, params);
		}
		/*IFJAVA8_START
		else if (TypeHelper.isAssignable(Temporal.class, inType)) {
			return JavaTimeConverterFactory.getConverterFrom(inType, outType, params);
		}
		IFJAVA8_END*/
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
