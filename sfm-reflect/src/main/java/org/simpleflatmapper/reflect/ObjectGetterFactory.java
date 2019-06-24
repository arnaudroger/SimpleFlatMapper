package org.simpleflatmapper.reflect;

import org.simpleflatmapper.reflect.asm.AsmFactory;
import org.simpleflatmapper.reflect.asm.AsmFactoryProvider;
import org.simpleflatmapper.reflect.getter.FieldGetter;
import org.simpleflatmapper.reflect.getter.MethodGetter;
import org.simpleflatmapper.reflect.getter.GetterHelper;
import org.simpleflatmapper.reflect.primitive.BooleanGetter;
import org.simpleflatmapper.reflect.primitive.BoxedBooleanGetter;
import org.simpleflatmapper.reflect.primitive.BoxedByteGetter;
import org.simpleflatmapper.reflect.primitive.BoxedCharacterGetter;
import org.simpleflatmapper.reflect.primitive.BoxedDoubleGetter;
import org.simpleflatmapper.reflect.primitive.BoxedFloatGetter;
import org.simpleflatmapper.reflect.primitive.BoxedIntGetter;
import org.simpleflatmapper.reflect.primitive.BoxedLongGetter;
import org.simpleflatmapper.reflect.primitive.BoxedShortGetter;
import org.simpleflatmapper.reflect.primitive.ByteGetter;
import org.simpleflatmapper.reflect.primitive.CharacterGetter;
import org.simpleflatmapper.reflect.primitive.DoubleGetter;
import org.simpleflatmapper.reflect.primitive.FloatGetter;
import org.simpleflatmapper.reflect.primitive.IntGetter;
import org.simpleflatmapper.reflect.primitive.LongGetter;
import org.simpleflatmapper.reflect.primitive.ShortGetter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.simpleflatmapper.util.Asserts.requireNonNull;


/**
 *
 */
public final class ObjectGetterFactory {
    private final AsmFactoryProvider asmFactory;

	public ObjectGetterFactory(AsmFactoryProvider asmFactory) {
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
		boolean accessible = Modifier.isPublic(method.getModifiers()) && Modifier.isPublic(method.getDeclaringClass().getModifiers());
		if (asmFactory != null && accessible) {
            try {
                return asmFactory.getAsmFactory(method.getDeclaringClass().getClassLoader()).createGetter(method);
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

    public <T, P> Getter<T, P> getFieldGetter(Field field) {
		requireNonNull("field", field);
		boolean accessible = Modifier.isPublic(field.getModifiers()) && Modifier.isPublic(field.getDeclaringClass().getModifiers());
		if (asmFactory != null && accessible) {
            try {
				AsmFactory asmFactory = this.asmFactory.getAsmFactory(field.getDeclaringClass().getClassLoader());
				if (asmFactory != null) {
					return asmFactory.createGetter(field);
				}
            } catch(Throwable e) {}
        }

        if (!accessible) {
        	try {
				field.setAccessible(true);
			} catch (Exception e) {
        		// cannot make field accessible
        		return null;
			}
        }
        return new FieldGetter<T, P>(field);
    }

    private Method lookForMethod(final Class<?> target, final String property) {
        if (target == null)  return null;

		for(Method m : target.getDeclaredMethods()) {
			if(GetterHelper.isPublicMember(m.getModifiers())
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
	public static <T> BooleanGetter<T> toBooleanGetter(final Getter<T, ? extends Boolean> getter) {
		if (getter instanceof BooleanGetter) {
			return (BooleanGetter<T>) getter;
		} else {
			return new BoxedBooleanGetter<T>(getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> IntGetter<T> toIntGetter(Getter<T, ? extends Integer> getter) {
		if (getter instanceof IntGetter) {
			return (IntGetter<T>) getter;
		} else {
			return new BoxedIntGetter<T>(getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> LongGetter<T> toLongGetter(Getter<T, ? extends Long> getter) {
		if (getter instanceof LongGetter) {
			return (LongGetter<T>) getter;
		} else {
			return new BoxedLongGetter<T>(getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> FloatGetter<T> toFloatGetter(Getter<T, ? extends Float> getter) {
		if (getter instanceof FloatGetter) {
			return (FloatGetter<T>) getter;
		} else {
			return new BoxedFloatGetter<T>(getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> DoubleGetter<T> toDoubleGetter(Getter<T, ? extends Double> getter) {
		if (getter instanceof DoubleGetter) {
			return (DoubleGetter<T>) getter;
		} else {
			return new BoxedDoubleGetter<T>(getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> ByteGetter<T> toByteGetter(Getter<T, ? extends Byte> getter) {
		if (getter instanceof ByteGetter) {
			return (ByteGetter<T>) getter;
		} else {
			return new BoxedByteGetter<T>(getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> ShortGetter<T> toShortGetter(Getter<T, ? extends Short> getter) {
		if (getter instanceof ShortGetter) {
			return (ShortGetter<T>) getter;
		} else {
			return new BoxedShortGetter<T>(getter);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> CharacterGetter<T> toCharGetter(Getter<T, ? extends Character> getter) {
		if (getter instanceof CharacterGetter) {
			return (CharacterGetter<T>) getter;
		} else {
			return new BoxedCharacterGetter<T>(getter);
		}
	}

}
