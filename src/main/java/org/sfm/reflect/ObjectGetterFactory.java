package org.sfm.reflect;

import org.sfm.reflect.asm.AsmFactory;
import org.sfm.reflect.impl.FieldGetter;
import org.sfm.reflect.impl.MethodGetter;
import org.sfm.reflect.primitive.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 *
 */
public final class ObjectGetterFactory {
    private final AsmFactory asmFactory;

	public ObjectGetterFactory(AsmFactory asmFactory) {
        this.asmFactory = asmFactory;
    }
	

	public <T, P> Getter<T, P> getGetter(final Class<? super T> target, final String property) {
		// first look for method
		final Method method = lookForMethod(target, property);
		final Getter<T, P> getter;
		if (method == null) {
            getter = getFieldGetter(target, property);
		} else {
            getter = getMethodGetter(method);
		}
		return getter;
	}

	public <T, P> Getter<T, P> getMethodGetter(final Method method) {
        if (asmFactory != null) {
            try {
                return asmFactory.createGetter(method);
            } catch(Exception e) {
                // ignore
            }
        }
        return new MethodGetter<T, P>(method);
	}

	public <T, P> Getter<T, P> getFieldGetter(final Class<?> target, final String property) {
		// look for field
		final Field field = lookForField(target, property);
		
		if (field != null) {
            return getFieldGetter(field);
		} else {
			return null;
		}
	}

    private <T, P> Getter<T, P> getFieldGetter(Field field) {

        if (asmFactory != null && Modifier.isPublic(field.getModifiers())) {
            try {
                return asmFactory.createGetter(field);
            } catch(Exception e) {}
        }

        if (!Modifier.isPublic(field.getModifiers())) {
            field.setAccessible(true);
        }
        return new FieldGetter<T, P>(field);
    }

    private Method lookForMethod(final Class<?> target, final String property) {
        if (target == null)  return null;

		for(Method m : target.getDeclaredMethods()) {
			if(GetterHelper.methodModifiersMatches(m.getModifiers())
					&& GetterHelper.methodNameMatchesProperty(m.getName(), property)) {
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
			if(GetterHelper.fieldModifiersMatches(field.getModifiers())
					&& GetterHelper.fieldNameMatchesProperty(field.getName(), property)) {
				return field;
			}
		}
		
		return lookForField(target.getSuperclass(), property);
	}

	@SuppressWarnings("unchecked")
	public static <T, P> BooleanGetter<T> toBooleanGetter(final Getter<T, P> getter) {
		if (getter instanceof BooleanGetter) {
			return (BooleanGetter<T>) getter;
		} else {
			return new BoxedBooleanGetter<T>((Getter<T, Boolean>) getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T, P> IntGetter<T> toIntGetter(Getter<T, P> getter) {
		if (getter instanceof IntGetter) {
			return (IntGetter<T>) getter;
		} else {
			return new BoxedIntGetter<T>((Getter<T, Integer>) getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T, P> LongGetter<T> toLongGetter(Getter<T, P> getter) {
		if (getter instanceof LongGetter) {
			return (LongGetter<T>) getter;
		} else {
			return new BoxedLongGetter<T>((Getter<T, Long>) getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T, P> FloatGetter<T> toFloatGetter(Getter<T, P> getter) {
		if (getter instanceof FloatGetter) {
			return (FloatGetter<T>) getter;
		} else {
			return new BoxedFloatGetter<T>((Getter<T, Float>) getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T, P> DoubleGetter<T> toDoubleGetter(Getter<T, P> getter) {
		if (getter instanceof DoubleGetter) {
			return (DoubleGetter<T>) getter;
		} else {
			return new BoxedDoubleGetter<T>((Getter<T, Double>) getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T, P> ByteGetter<T> toByteGetter(Getter<T, P> getter) {
		if (getter instanceof ByteGetter) {
			return (ByteGetter<T>) getter;
		} else {
			return new BoxedByteGetter<T>((Getter<T, Byte>) getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T, P> ShortGetter<T> toShortGetter(Getter<T, P> getter) {
		if (getter instanceof ShortGetter) {
			return (ShortGetter<T>) getter;
		} else {
			return new BoxedShortGetter<T>((Getter<T, Short>) getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T, P> CharacterGetter<T> toCharGetter(Getter<T, P> getter) {
		if (getter instanceof CharacterGetter) {
			return (CharacterGetter<T>) getter;
		} else {
			return new BoxedCharacterGetter<T>((Getter<T, Character>) getter);
		}
	}

}
