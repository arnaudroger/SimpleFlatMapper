package org.sfm.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

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

}
