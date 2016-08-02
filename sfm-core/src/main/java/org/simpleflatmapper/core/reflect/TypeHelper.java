package org.simpleflatmapper.core.reflect;

import org.simpleflatmapper.core.tuples.Tuple2;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

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
		} else  {
			Type[] parameterTypes = getGenericParameterForClass(outType, Collection.class);
			if (parameterTypes != null) {
				return parameterTypes[0];
			}
		}
		return null;
	}

	public static Tuple2<Type, Type> getKeyValueTypeOfMap(Type outType) {
		Type[] parameterTypes = getGenericParameterForClass(outType, Map.class);
		if (parameterTypes != null) {
			return new Tuple2<Type, Type>(parameterTypes[0], parameterTypes[1]);
		}
		return null;
	}


	private static Type getGenericInterface(Type t, Class<?> i) {
		if (TypeHelper.areEquals(t, i)) {
			return t;
		}
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

	private static Type getGenericSuperType(Type t) {
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

	public static boolean isAssignable(Type type, Type from) {
		return isAssignable(TypeHelper.toBoxedClass(type), from);
	}

	public static boolean isAssignable(Class<?> class1, Type from) {
		return class1.isAssignableFrom(toBoxedClass(from));
	}

	public static boolean isJavaLang(Type target) {
        Class<?> clazz = TypeHelper.toClass(target);
        return clazz.isPrimitive() || clazz.getPackage().getName().equals("java.lang");
	}

    public static boolean isEnum(Type target) {
        Class<?> clazz = TypeHelper.toClass(target);
        return clazz.isEnum();
    }

	public static Class<?> toBoxedClass(Type type) {
		return TypeHelper.toBoxedClass(toClass(type));
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
	public static boolean areEquals(Type target, Type clazz) {
		return TypeHelper.toClass(clazz).equals(TypeHelper.toClass(target));
	}

	public static Type[] getGenericParameterForClass(Type type, Class<?> interfaceClass) {

		if (isAssignable(interfaceClass, type)) {
			// first look for the interface
			Type genericInterface = getGenericInterface(type, interfaceClass);

			final Type[] types;
			if (genericInterface != null) {
				if (genericInterface instanceof ParameterizedType) {
					types = ((ParameterizedType) genericInterface).getActualTypeArguments();
				} else {
					throw new IllegalStateException("type " + type + " is not a ParameterizedType");
				}
			} else {
				types = getGenericParameterForClass(TypeHelper.getGenericSuperType(type), interfaceClass);
			}
			resolveTypeVariables(type, types);
			return types;
		} else {
			throw new IllegalArgumentException("type " + type + " does not implement/extends " + interfaceClass);
		}
	}

	private static void resolveTypeVariables(Type source, Type[] types) {
		for(int i = 0; i < types.length; i++) {
            Type t = types[i];
            if (t instanceof TypeVariable) {
                types[i] = resolveTypeVariable(source, (TypeVariable) t);
            }
        }
	}

	public static Type resolveTypeVariable(Type type, TypeVariable t) {
		TypeVariable<Class<Object>>[] typeParameters = TypeHelper.toClass(type).getTypeParameters();

		for(int i = 0; i < typeParameters.length; i++) {
			TypeVariable<Class<Object>> typeVariable = typeParameters[i];
			if (typeVariable.getName().equals(t.getName())) {
				return ((ParameterizedType)type).getActualTypeArguments()[i];
			}
		}
		return type;
	}
}
