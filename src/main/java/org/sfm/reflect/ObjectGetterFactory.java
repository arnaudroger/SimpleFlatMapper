package org.sfm.reflect;

import org.sfm.reflect.asm.AsmFactory;
import org.sfm.reflect.impl.FieldGetter;
import org.sfm.reflect.impl.MethodGetter;

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

}
