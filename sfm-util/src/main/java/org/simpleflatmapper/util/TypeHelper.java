package org.simpleflatmapper.util;


import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.*;

public final class TypeHelper {

	private TypeHelper() {}

	@SuppressWarnings("unchecked")
	public static <T> Class<T> toClass(Type target) {
		if (target instanceof Class) {
			return (Class<T>) target;
		} else if (target instanceof ParameterizedType) {
			return toClass(((ParameterizedType) target).getRawType());
		} else if (target instanceof TypeVariable) {
			return toClass(((TypeVariable) target).getBounds()[0]);
		} else if (target instanceof WildcardType) {
			return toClass(((WildcardType)target).getUpperBounds()[0]);
		} else if (target instanceof GenericArrayType) {
		    return (Class<T>) Array.newInstance(toClass(((GenericArrayType) target).getGenericComponentType()), 0).getClass();
        }
		throw new UnsupportedOperationException("Cannot extract class from type " + target + " " + target.getClass());
	}

	public static ClassLoader getClassLoader(Type target, ClassLoader defaultClassLoader) {
		if (target == null) return defaultClassLoader;
		Class<?> clazz = toClass(target);
		if (clazz == null) return defaultClassLoader;
		return clazz.getClassLoader();
	}


	public static <T> Map<TypeVariable<?>, Type> getTypesMap(Type targetType) {
		Class<T> targetClass = TypeHelper.toClass(targetType);
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
		wrappers.put(void.class, Void.class);
	}

	public static boolean isArray(Type outType) {
		return TypeHelper.toClass(outType).isArray();
	}

	public static Type getComponentTypeOfListOrArray(Type outType) {
		Class<?> target = toClass(outType);
		if (target.isArray()) {
			return toClass(outType).getComponentType();
		} else  {
			Type[] parameterTypes = getGenericParameterForClass(outType, Iterable.class);
			if (parameterTypes != null) {
				Type parameterType = parameterTypes[0];
				if (parameterType != null) {
					return parameterType;
				}
			}
		}
		return Object.class;
	}

	public static MapEntryTypes getKeyValueTypeOfMap(Type outType) {
		Type[] parameterTypes = getGenericParameterForClass(outType, Map.class);
		if (parameterTypes != null) {
			return new MapEntryTypes(parameterTypes[0], parameterTypes[1]);
		}
		return MapEntryTypes.OBJECT_OBJECT;
	}


	private static Type getGenericInterface(Type t, Class<?> i) {
		if (TypeHelper.areEquals(t, i)) {
			return t;
		}
		Type[] genericInterfaces = TypeHelper.toClass(t).getGenericInterfaces();
		for(Type it : genericInterfaces) {
			if (isAssignable(i, it)) {
				if (areEquals(it, i)) {
					return it;
				} else {
					return getGenericInterface(it, i);
				}
			}
		}
		return null;
	}

	private static Type getGenericSuperType(Type t) {
		return TypeHelper.toClass(t).getGenericSuperclass();
	}



	public static boolean isAssignable(Type type, Type from) {
		return isAssignable(TypeHelper.toBoxedClass(type), from);
	}

	public static boolean isAssignable(Class<?> class1, Type from) {
		return class1.isAssignableFrom(toBoxedClass(from));
	}

	public static boolean isJavaLang(Type target) {
        Class<?> clazz = TypeHelper.toClass(target);
        return clazz.isPrimitive() || (clazz.getPackage() != null && clazz.getPackage().getName().equals("java.lang"));
	}

	public static boolean isInPackage(Type target, Predicate<String> packagePredicate) {
		Class<?> clazz = TypeHelper.toClass(target);
		Package clazzPackage = clazz.getPackage();
		if (clazzPackage != null) {
			return packagePredicate.test(clazzPackage.getName());
		}
		return false;
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
			Class<?> clazz = wrappers.get(target);
			if (clazz == null) {
				throw new RuntimeException("Unexpected primitive type " + target);
			}
			return clazz;
		} else {
			return target;
		}
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
					return null;
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


	public static void resolveTypeVariables(Type source, Type[] types) {
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
				if (type instanceof ParameterizedType) {
					return ((ParameterizedType) type).getActualTypeArguments()[i];
				} else {
					return Object.class;
				}
			}
		}
		if (typeParameters.length == 1 && type instanceof ParameterizedType && ((ParameterizedType) type).getActualTypeArguments().length == 1) {
			return ((ParameterizedType) type).getActualTypeArguments()[0];
		}
		return Object.class;
	}

	public static boolean isKotlinClass(Type target) {
		Annotation[] annotations = TypeHelper.toClass(target).getDeclaredAnnotations();
		if (annotations != null) {
			for(int i = 0; i < annotations.length;i++) {
				Annotation a = annotations[i];
				if (a.annotationType().getName().equals("kotlin.Metadata")) {
					return true;
				}
			}
		}
		return false;
	}


	public static class MapEntryTypes {
		public static final MapEntryTypes OBJECT_OBJECT = new MapEntryTypes(Object.class, Object.class);
		private final Type keyType;
		private final Type valueType;
		public MapEntryTypes(Type keyType, Type valueType) {
			this.keyType = keyType;
			this.valueType = valueType;
		}

		public Type getKeyType() {
			return keyType;
		}

		public Type getValueType() {
			return valueType;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			MapEntryTypes that = (MapEntryTypes) o;

			if (keyType != null ? !keyType.equals(that.keyType) : that.keyType != null) return false;
			return valueType != null ? valueType.equals(that.valueType) : that.valueType == null;

		}

		@Override
		public int hashCode() {
			int result = keyType != null ? keyType.hashCode() : 0;
			result = 31 * result + (valueType != null ? valueType.hashCode() : 0);
			return result;
		}

		@Override
		public String toString() {
			return "MapEntryTypes{" +
					"keyType=" + keyType +
					", valueType=" + valueType +
					'}';
		}
	}
}
