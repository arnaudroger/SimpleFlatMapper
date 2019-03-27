package org.simpleflatmapper.reflect;

import org.simpleflatmapper.reflect.asm.AsmFactoryProvider;
import org.simpleflatmapper.reflect.getter.FieldSetter;
import org.simpleflatmapper.reflect.setter.MethodSetter;
import org.simpleflatmapper.reflect.setter.NullSetter;
import org.simpleflatmapper.reflect.setter.SetterHelper;
import org.simpleflatmapper.reflect.primitive.BooleanFieldSetter;
import org.simpleflatmapper.reflect.primitive.BooleanMethodSetter;
import org.simpleflatmapper.reflect.primitive.BooleanSetter;
import org.simpleflatmapper.reflect.primitive.ByteFieldSetter;
import org.simpleflatmapper.reflect.primitive.ByteMethodSetter;
import org.simpleflatmapper.reflect.primitive.ByteSetter;
import org.simpleflatmapper.reflect.primitive.CharacterFieldSetter;
import org.simpleflatmapper.reflect.primitive.CharacterMethodSetter;
import org.simpleflatmapper.reflect.primitive.CharacterSetter;
import org.simpleflatmapper.reflect.primitive.DoubleFieldSetter;
import org.simpleflatmapper.reflect.primitive.DoubleMethodSetter;
import org.simpleflatmapper.reflect.primitive.DoubleSetter;
import org.simpleflatmapper.reflect.primitive.FloatFieldSetter;
import org.simpleflatmapper.reflect.primitive.FloatMethodSetter;
import org.simpleflatmapper.reflect.primitive.FloatSetter;
import org.simpleflatmapper.reflect.primitive.IntFieldSetter;
import org.simpleflatmapper.reflect.primitive.IntMethodSetter;
import org.simpleflatmapper.reflect.primitive.IntSetter;
import org.simpleflatmapper.reflect.primitive.LongFieldSetter;
import org.simpleflatmapper.reflect.primitive.LongMethodSetter;
import org.simpleflatmapper.reflect.primitive.LongSetter;
import org.simpleflatmapper.reflect.primitive.ShortFieldSetter;
import org.simpleflatmapper.reflect.primitive.ShortMethodSetter;
import org.simpleflatmapper.reflect.primitive.ShortSetter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 *
 */
public final class ObjectSetterFactory {
	
	private final AsmFactoryProvider asmFactoryProvider;
	
	public ObjectSetterFactory(final AsmFactoryProvider asmFactoryProvider) {
		this.asmFactoryProvider = asmFactoryProvider;
	}

	public <T, P> Setter<T, P> getSetter(final Class<? extends T> target, final String property) {
		// first look for method
		final Method method = lookForMethod(target, property);
		final Setter<T, P> setter;
		if (method == null) {
			setter = getFieldSetter(target, property);
		} else {
			setter = getMethodSetter(method);
		}
		return setter;
	}

	public <T, P> Setter<T, P> getMethodSetter(final Method method) {
		boolean accessible = Modifier.isPublic(method.getModifiers()) && Modifier.isPublic(method.getDeclaringClass().getModifiers());
		if (asmFactoryProvider != null && accessible) {
			try {
				return asmFactoryProvider.getAsmFactory(method.getDeclaringClass().getClassLoader()).createSetter(method);
			} catch(Throwable e) {
                // ignore
			}
		}
		if (!accessible) {
			try {
				method.setAccessible(true);
			} catch (Exception e) {
				// cannot make field accessible
				return null;
			}
		}
        return new MethodSetter<T, P>(method);
	}

	public <T, P> Setter<T, P> getFieldSetter(final Class<?> target, final String property) {
		// look for field
		final Field field = lookForField(target, property);
		
		if (field != null) {
            return getFieldSetter(field);
		} else {
			return null;
		}
	}

    public <T, P> Setter<T, P> getFieldSetter(Field field) {
		boolean accessible = Modifier.isPublic(field.getModifiers()) && Modifier.isPublic(field.getDeclaringClass().getModifiers());
		if (asmFactoryProvider != null && accessible) {
            try {
                return asmFactoryProvider.getAsmFactory(field.getDeclaringClass().getClassLoader()).createSetter(field);
            } catch(Throwable e) {
            }
        }
        if (!accessible) {
        	try {
				field.setAccessible(true);
			} catch (Exception e) {
				// cannot make field accessible
				return null;
			}
        }
        return new FieldSetter<T, P>(field);
    }

    private Method lookForMethod(final Class<?> target, final String property) {
        if (target == null)  return null;

		for(Method m : target.getDeclaredMethods()) {
			if(SetterHelper.isSetter(m)
					&& SetterHelper.methodNameMatchesProperty(m.getName(), property)) {
				return m;
			}
		}
		
		if (!Object.class.equals(target)) {
			return lookForMethod(target.getSuperclass(), property);
		}
		
		return null;
	}
	

	private Field lookForField(final Class<?> target, final String property) {
        if (target == null)  return null;

		for(Field field : target.getDeclaredFields()) {
			if(SetterHelper.fieldModifiersMatches(field.getModifiers())
					&& SetterHelper.fieldNameMatchesProperty(field.getName(), property)) {
				return field;
			}
		}
		
		if (!Object.class.equals(target)) {
			return lookForField(target.getSuperclass(), property);
		}
		
		return null;
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> BooleanSetter<? super T> toBooleanSetter(final Setter<? super T, ? super Boolean> setter) {
		if (isNullSetter(setter)) {
			return null;
		} else if (setter instanceof BooleanSetter) {
			return (BooleanSetter<? super T>) setter;
		} else if (setter instanceof MethodSetter) {
			return new BooleanMethodSetter<T>(((MethodSetter) setter).getMethod());
		} else if (setter instanceof FieldSetter) {
			return new BooleanFieldSetter<T>(((FieldSetter) setter).getField());
		} else {
			throw new IllegalArgumentException("Invalid type " + setter);
		}
	}

	private static boolean isNullSetter(Setter<?, ?> setter) {
		return NullSetter.isNull(setter);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> ByteSetter<? super T> toByteSetter(final Setter<? super T, ? super Byte> setter) {
		if (isNullSetter(setter)) {
			return null;
		} else if (setter instanceof ByteSetter) {
			return (ByteSetter<? super T>) setter;
		} else if (setter instanceof MethodSetter) {
			return new ByteMethodSetter<T>(((MethodSetter) setter).getMethod());
		} else if (setter instanceof FieldSetter) {
			return new ByteFieldSetter<T>(((FieldSetter) setter).getField());
		} else {
			throw new IllegalArgumentException("Invalid type " + setter);
		}
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> CharacterSetter<? super T> toCharacterSetter(final Setter<? super T, ? super Character> setter) {
		if (isNullSetter(setter)) {
			return null;
		} else if (setter instanceof CharacterSetter) {
			return (CharacterSetter<? super T>) setter;
		} else if (setter instanceof MethodSetter) {
			return new CharacterMethodSetter<T>(((MethodSetter) setter).getMethod());
		} else if (setter instanceof FieldSetter) {
			return new CharacterFieldSetter<T>(((FieldSetter) setter).getField());
		} else {
			throw new IllegalArgumentException("Invalid type " + setter);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> ShortSetter<? super T> toShortSetter(final Setter<? super T, ? super Short> setter) {
		if (isNullSetter(setter)) {
			return null;
		} else if (setter instanceof ShortSetter) {
			return (ShortSetter<? super T>) setter;
		} else if (setter instanceof MethodSetter) {
			return new ShortMethodSetter<T>(((MethodSetter) setter).getMethod());
		} else if (setter instanceof FieldSetter) {
			return new ShortFieldSetter<T>(((FieldSetter) setter).getField());
		} else {
			throw new IllegalArgumentException("Invalid type " + setter);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> IntSetter<? super T> toIntSetter(final Setter<? super T, ? super Integer> setter) {
		if (isNullSetter(setter)) {
			return null;
		} else if (setter instanceof IntSetter) {
			return (IntSetter<? super T>) setter;
		} else if (setter instanceof MethodSetter) {
			return new IntMethodSetter<T>(((MethodSetter) setter).getMethod());
		} else if (setter instanceof FieldSetter) {
			return new IntFieldSetter<T>(((FieldSetter) setter).getField());
		} else {
			throw new IllegalArgumentException("Invalid type " + setter);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> LongSetter<? super T> toLongSetter(final Setter<? super T, ? super Long> setter) {
		if (isNullSetter(setter)) {
			return null;
		} else if (setter instanceof LongSetter) {
			return (LongSetter<? super T>) setter;
		} else if (setter instanceof MethodSetter) {
			return new LongMethodSetter<T>(((MethodSetter) setter).getMethod());
		} else if (setter instanceof FieldSetter) {
			return new LongFieldSetter<T>(((FieldSetter) setter).getField());
		} else {
			throw new IllegalArgumentException("Invalid type " + setter);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> FloatSetter<? super T> toFloatSetter(final Setter<? super T, ? super Float> setter) {
		if (isNullSetter(setter)) {
			return null;
		} else if (setter instanceof FloatSetter) {
			return (FloatSetter<? super T>) setter;
		} else if (setter instanceof MethodSetter) {
			return new FloatMethodSetter<T>(((MethodSetter) setter).getMethod());
		} else if (setter instanceof FieldSetter) {
			return new FloatFieldSetter<T>(((FieldSetter) setter).getField());
		} else {
			throw new IllegalArgumentException("Invalid type " + setter);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> DoubleSetter<? super T> toDoubleSetter(final Setter<? super T, ? super Double> setter) {
		if (isNullSetter(setter)) {
			return null;
		} else if (setter instanceof DoubleSetter) {
			return (DoubleSetter<? super T>) setter;
		} else if (setter instanceof MethodSetter) {
			return new DoubleMethodSetter<T>(((MethodSetter) setter).getMethod());
		} else if (setter instanceof FieldSetter) {
			return new DoubleFieldSetter<T>(((FieldSetter) setter).getField());
		} else {
			throw new IllegalArgumentException("Invalid type " + setter);
		}
	}

}
