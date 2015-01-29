package org.sfm.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
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
	
	public static Class<?> wrap(Type type) {
		return wrap(TypeHelper.toClass(type));
	}
	
	public static  boolean areCompatible(Class<?> target, Class<?> source) {
		Class<?> wrapTarget = wrap(target);
		Class<?> wrapSource = wrap(source);
		return wrapTarget.isAssignableFrom(wrapSource);
	}
	
	public static boolean isNumber(Type target) {
		return Number.class.isAssignableFrom(wrap(TypeHelper.toClass(target)));
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

	public static boolean isArray(Type outType) {
		return TypeHelper.toClass(outType).isArray();
	}

	public static Type getComponentType(Type outType) {
		Class<?> target = toClass(outType);
		if (target.isArray()) {
			return toClass(outType).getComponentType();
		} else if (outType instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) outType;
			return pt.getActualTypeArguments()[0];
		}
		return null;
	}


	public static Type[] getParamTypesForInterface(Class<?> target, Class<?> inter) {
		if (target == null) {
			return null;
		}
		Type[] genericInterfaces = target.getGenericInterfaces();
		for(Type t : genericInterfaces) {
			if (t instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) t;
				if (pt.getRawType().equals(inter)) {
					return pt.getActualTypeArguments();
				}
			} else if (t instanceof Class) {
				Type[] readerClass = getParamTypesForInterface((Class) t, inter);
				if (readerClass != null) {
					return readerClass;
				}
			}
		}
		return getParamTypesForInterface(target.getSuperclass(), inter);
	}

	public static boolean isClass(Type outType, Class<?> class1) {
		return toClass(outType).equals(class1);
	}

	public static boolean isAssignable(Class<?> class1, Type from) {
		return class1.isAssignableFrom(toClass(from));
	}

	public static boolean isJavaLang(Type target) {
		return TypeHelper.toClass(target).getPackage().getName().equals("java.lang");
	}

	public static Class<?> toBoxedClass(Class<?> target) {
		if (target.isPrimitive()) {
			return wrappers.get(target);
		} else {
			return target;
		}
	}
}
