package org.sfm.reflect;

import org.sfm.tuples.Tuple2;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeHelper {

	@SuppressWarnings("unchecked")
	public static <T> Class<T> toClass(Type target) {
		if (target instanceof Class) {
			return (Class<T>) target;
		} else if (target instanceof ParameterizedType) {
			return (Class<T>) ((ParameterizedType) target).getRawType();
		} else if (target instanceof TypeVariable) {
			Type[] bounds = ((TypeVariable) target).getBounds();
			return (Class<T>) bounds[0];
		}
		throw new UnsupportedOperationException("Cannot extract class from type " + target);
	}

	public static <T> Map<TypeVariable<?>, Type> getTypesMap(Type targetType, Class<T> targetClass) {
		Map<TypeVariable<?>, Type> genericTypes = Collections.emptyMap();
		if (targetType instanceof ParameterizedType) {
			TypeVariable<Class<T>>[] typeParameters = targetClass.getTypeParameters();
			Type[] actualTypeArguments = ((ParameterizedType) targetType).getActualTypeArguments();

			genericTypes = new HashMap<TypeVariable<?>, Type>();
			for (int i = 0; i < typeParameters.length; i++) {
				TypeVariable<?> typeParameter = typeParameters[i];
				Type typeArgument = actualTypeArguments[i];
				genericTypes.put(typeParameter, typeArgument);
			}
		}

		return genericTypes;
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

	private final static Map<Class<?>, Class<?>> wrappers = new HashMap<Class<?>, Class<?>>();
	static {
		wrappers.put(boolean.class, Boolean.class);
		wrappers.put(byte.class, Byte.class);
		wrappers.put(short.class, Short.class);
		wrappers.put(char.class, Character.class);
		wrappers.put(int.class, Integer.class);
		wrappers.put(long.class, Long.class);
		wrappers.put(float.class, Float.class);
		wrappers.put(double.class, Double.class);
	}

	public static boolean isArray(Type outType) {
		return TypeHelper.toClass(outType).isArray();
	}

	public static Type getComponentTypeOfListOrArray(Type outType) {
		Class<?> target = toClass(outType);
		if (target.isArray()) {
			return toClass(outType).getComponentType();
		} else if (outType instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) outType;
			Type rt = pt.getRawType();

			if (rt.equals(List.class)) {
				return pt.getActualTypeArguments()[0];
			}
		} else if (outType instanceof Class) {
			Type cc = outType;

			while(cc != null) {
				Type lt = getGenericInterface(cc, List.class);
				if (lt != null && cc instanceof ParameterizedType) {
					return ((ParameterizedType) cc).getActualTypeArguments()[0];
				}
				cc = getGenericSuperType(cc);
			}
		}
		return null;
	}

	public static Tuple2<Type, Type> getKeyValueTypeOfMap(Type outType) {
		if (outType instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) outType;
			Type rt = pt.getRawType();

			if (rt.equals(Map.class)) {
				return new Tuple2<Type, Type>(pt.getActualTypeArguments()[0], pt.getActualTypeArguments()[1]);
			}
		} else if (outType instanceof Class) {
			Type cc = outType;

			while(cc != null) {
				Type lt = getGenericInterface(cc, Map.class);
				if (lt != null && cc instanceof ParameterizedType) {
					return new Tuple2<Type, Type>(((ParameterizedType) cc).getActualTypeArguments()[0], ((ParameterizedType) cc).getActualTypeArguments()[1]);
				}
				cc = getGenericSuperType(cc);
			}
		}
		return null;
	}


	private static Type getGenericInterface(Type t, Class<?> i) {
		if (t instanceof Class) {
			for(Type it : ((Class) t).getGenericInterfaces()) {
				if (isAssignable(i, it)) {
					return it;
				}
			}
		} else if (t instanceof ParameterizedType) {
			return getGenericInterface(((ParameterizedType) t).getRawType(), i);
		}
		return null;
	}

	public static Type getGenericSuperType(Type t) {
		if (t instanceof Class) {
			return ((Class) t).getGenericSuperclass();
		} else if (t instanceof ParameterizedType) {
			return getGenericSuperType(((ParameterizedType) t).getRawType());
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
        Class<?> clazz = TypeHelper.toClass(target);
        return clazz.isPrimitive() || clazz.getPackage().getName().equals("java.lang");
	}

    public static boolean isEnum(Type target) {
        Class<?> clazz = TypeHelper.toClass(target);
        return clazz.isEnum();
    }

	public static Class<?> toBoxedClass(Class<?> target) {
		if (target.isPrimitive()) {
			return wrappers.get(target);
		} else {
			return target;
		}
	}

    public static boolean areEquals(Type target, Class<?> clazz) {
        return clazz.equals(TypeHelper.toClass(target));
    }
}
