package org.sfm.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TypeHelper {

	@SuppressWarnings("unchecked")
	public static <T> Class<T> toClass(Type target) {
		if (target instanceof Class) {
			return (Class<T>) target;
		} else if (target instanceof ParameterizedType) {
			return (Class<T>) ((ParameterizedType) target).getRawType();
		}
		throw new UnsupportedOperationException("Cannot extract class from type " + target);
	}

	public static boolean isPrimitive(Type type) {
		return toClass(type).isPrimitive();
	}
	
	public static Class<?> wrap(Class<?> target) {
		if (target.isPrimitive()) {
			return wrappers.get(target);
		} else {
			return target;
		}
	}
	
	public static  boolean areCompatible(Class<?> target, Class<?> source) {
		Class<?> wrapTarget = wrap(target);
		Class<?> wrapSource = wrap(source);
		return wrapTarget.isAssignableFrom(wrapSource);
	}
	
	public static boolean isNumber(Class<?> target) {
		return Number.class.isAssignableFrom(wrap(target));
	}

	@SuppressWarnings("serial")
	private final static Map<Class<?>, Class<?>> wrappers = new HashMap<Class<?>, Class<?>>() {{
		put(boolean.class, Boolean.class);
		put(byte.class, Byte.class);
		put(short.class, Short.class);
		put(char.class, Character.class);
		put(int.class, Integer.class);
		put(long.class, Long.class);
		put(float.class, Float.class);
		put(double.class, Double.class);
	}};
	
}
