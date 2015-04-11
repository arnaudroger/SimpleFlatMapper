package org.sfm.reflect;

import org.sfm.reflect.asm.AsmFactory;
import org.sfm.reflect.impl.FieldSetter;
import org.sfm.reflect.impl.MethodSetter;
import org.sfm.reflect.impl.NullSetter;
import org.sfm.reflect.primitive.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class ObjectSetterFactory {
	
	private final AsmFactory asmFactory;
	
	public ObjectSetterFactory(final AsmFactory asmFactory) {
		this.asmFactory = asmFactory;
	}
	
	public <T, P, C extends T> Setter<T, P> getSetter(final Class<C> target, final String property) {
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
		if (asmFactory != null) {
			try {
				return asmFactory.createSetter(method);
			} catch(Exception e) {
                // ignore
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

    private <T, P> Setter<T, P> getFieldSetter(Field field) {
        if (asmFactory != null && Modifier.isPublic(field.getModifiers())) {
            try {
                return asmFactory.createSetter(field);
            } catch(Exception e) {
            }
        }
        if (!Modifier.isPublic(field.getModifiers())) {
            field.setAccessible(true);
        }
        return new FieldSetter<T, P>(field);
    }

    private Method lookForMethod(final Class<?> target, final String property) {
        if (target == null)  return null;

		for(Method m : target.getDeclaredMethods()) {
			if(SetterHelper.methodModifiersMatches(m.getModifiers())
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
	public static <T, P> BooleanSetter<T> toBooleanSetter(final Setter<T, P> setter) {
		if (isNullSetter(setter)) {
			return null;
		} else if (setter instanceof BooleanSetter) {
			return (BooleanSetter<T>) setter;
		} else if (setter instanceof MethodSetter) {
			return new BooleanMethodSetter<T>(((MethodSetter) setter).getMethod());
		} else if (setter instanceof FieldSetter) {
			return new BooleanFieldSetter<T>(((FieldSetter) setter).getField());
		} else {
			throw new IllegalArgumentException("Invalid type " + setter);
		}
	}

	private static boolean isNullSetter(Setter<?, ?> setter) {
		return setter == null || setter instanceof NullSetter;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T, P> ByteSetter<T> toByteSetter(final Setter<T, P> setter) {
		if (isNullSetter(setter)) {
			return null;
		} else if (setter instanceof ByteSetter) {
			return (ByteSetter<T>) setter;
		} else if (setter instanceof MethodSetter) {
			return new ByteMethodSetter<T>(((MethodSetter) setter).getMethod());
		} else if (setter instanceof FieldSetter) {
			return new ByteFieldSetter<T>(((FieldSetter) setter).getField());
		} else {
			throw new IllegalArgumentException("Invalid type " + setter);
		}
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T, P> CharacterSetter<T> toCharacterSetter(final Setter<T, P> setter) {
		if (isNullSetter(setter)) {
			return null;
		} else if (setter instanceof CharacterSetter) {
			return (CharacterSetter<T>) setter;
		} else if (setter instanceof MethodSetter) {
			return new CharacterMethodSetter<T>(((MethodSetter) setter).getMethod());
		} else if (setter instanceof FieldSetter) {
			return new CharacterFieldSetter<T>(((FieldSetter) setter).getField());
		} else {
			throw new IllegalArgumentException("Invalid type " + setter);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T, P> ShortSetter<T> toShortSetter(final Setter<T, P> setter) {
		if (isNullSetter(setter)) {
			return null;
		} else if (setter instanceof ShortSetter) {
			return (ShortSetter<T>) setter;
		} else if (setter instanceof MethodSetter) {
			return new ShortMethodSetter<T>(((MethodSetter) setter).getMethod());
		} else if (setter instanceof FieldSetter) {
			return new ShortFieldSetter<T>(((FieldSetter) setter).getField());
		} else {
			throw new IllegalArgumentException("Invalid type " + setter);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T, P> IntSetter<T> toIntSetter(final Setter<T, P> setter) {
		if (isNullSetter(setter)) {
			return null;
		} else if (setter instanceof IntSetter) {
			return (IntSetter<T>) setter;
		} else if (setter instanceof MethodSetter) {
			return new IntMethodSetter<T>(((MethodSetter) setter).getMethod());
		} else if (setter instanceof FieldSetter) {
			return new IntFieldSetter<T>(((FieldSetter) setter).getField());
		} else {
			throw new IllegalArgumentException("Invalid type " + setter);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T, P> LongSetter<T> toLongSetter(final Setter<T, P> setter) {
		if (isNullSetter(setter)) {
			return null;
		} else if (setter instanceof LongSetter) {
			return (LongSetter<T>) setter;
		} else if (setter instanceof MethodSetter) {
			return new LongMethodSetter<T>(((MethodSetter) setter).getMethod());
		} else if (setter instanceof FieldSetter) {
			return new LongFieldSetter<T>(((FieldSetter) setter).getField());
		} else {
			throw new IllegalArgumentException("Invalid type " + setter);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T, P> FloatSetter<T> toFloatSetter(final Setter<T, P> setter) {
		if (isNullSetter(setter)) {
			return null;
		} else if (setter instanceof FloatSetter) {
			return (FloatSetter<T>) setter;
		} else if (setter instanceof MethodSetter) {
			return new FloatMethodSetter<T>(((MethodSetter) setter).getMethod());
		} else if (setter instanceof FieldSetter) {
			return new FloatFieldSetter<T>(((FieldSetter) setter).getField());
		} else {
			throw new IllegalArgumentException("Invalid type " + setter);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T, P> DoubleSetter<T> toDoubleSetter(final Setter<T, P> setter) {
		if (isNullSetter(setter)) {
			return null;
		} else if (setter instanceof DoubleSetter) {
			return (DoubleSetter<T>) setter;
		} else if (setter instanceof MethodSetter) {
			return new DoubleMethodSetter<T>(((MethodSetter) setter).getMethod());
		} else if (setter instanceof FieldSetter) {
			return new DoubleFieldSetter<T>(((FieldSetter) setter).getField());
		} else {
			throw new IllegalArgumentException("Invalid type " + setter);
		}
	}

}
