package org.sfm.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.sfm.jdbc.AsmHelper;
import org.sfm.reflect.asm.AsmFactory;
import org.sfm.reflect.primitive.BooleanFieldSetter;
import org.sfm.reflect.primitive.BooleanMethodSetter;
import org.sfm.reflect.primitive.BooleanSetter;
import org.sfm.reflect.primitive.ByteFieldSetter;
import org.sfm.reflect.primitive.ByteMethodSetter;
import org.sfm.reflect.primitive.ByteSetter;
import org.sfm.reflect.primitive.CharacterFieldSetter;
import org.sfm.reflect.primitive.CharacterMethodSetter;
import org.sfm.reflect.primitive.CharacterSetter;
import org.sfm.reflect.primitive.DoubleFieldSetter;
import org.sfm.reflect.primitive.DoubleMethodSetter;
import org.sfm.reflect.primitive.DoubleSetter;
import org.sfm.reflect.primitive.FloatFieldSetter;
import org.sfm.reflect.primitive.FloatMethodSetter;
import org.sfm.reflect.primitive.FloatSetter;
import org.sfm.reflect.primitive.IntFieldSetter;
import org.sfm.reflect.primitive.IntMethodSetter;
import org.sfm.reflect.primitive.IntSetter;
import org.sfm.reflect.primitive.LongFieldSetter;
import org.sfm.reflect.primitive.LongMethodSetter;
import org.sfm.reflect.primitive.LongSetter;
import org.sfm.reflect.primitive.ShortFieldSetter;
import org.sfm.reflect.primitive.ShortMethodSetter;
import org.sfm.reflect.primitive.ShortSetter;

public final class SetterFactory {
	
	private final AsmFactory asmFactory;
	
	public SetterFactory(final AsmFactory asmSetterFactory) {
		this.asmFactory = asmSetterFactory;
	}
	
	public SetterFactory() {
		this.asmFactory = AsmHelper.getAsmSetterFactory();
	}

	public AsmFactory getAsmFactory() {
		return asmFactory;
	}

	public <T, P, C extends T> Setter<T, P> getSetter(final Class<C> target, final String property) {
		// first look for method
		final Method method = lookForMethod(target, property);
		
		if (method == null) {
			return getFieldSetter(target, property);
		} else {
			return getMethodSetter(method);
		}
	}

	public <T, P, C extends T> Setter<T, P> getMethodSetter(final Method method) {
		if (asmFactory != null) {
			try {
				return asmFactory.createSetter(method);
			} catch(Exception e) {
				return new MethodSetter<T, P>(method);
			}
		} else {
			return new MethodSetter<T, P>(method);
		}
	}

	public <T, P, C extends T> FieldSetter<T, P> getFieldSetter(final Class<C> target, final String property) {
		// look for field
		final Field field = lookForField(target, property);
		
		if (field != null) {
			field.setAccessible(true);
			return new FieldSetter<T, P>(field);
		} else {
			return null;
		}
	}

	private Method lookForMethod(final Class<?> target, final String property) {
		
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
	public <T, P> BooleanSetter<T> toBooleanSetter(final Setter<T, P> setter) {
		if (setter instanceof BooleanSetter) {
			return (BooleanSetter<T>) setter;
		} else if (setter instanceof MethodSetter) {
			return new BooleanMethodSetter<T>(((MethodSetter) setter).getMethod());
		} else if (setter instanceof FieldSetter) {
			return new BooleanFieldSetter<T>(((FieldSetter) setter).getField());
		} else {
			throw new IllegalArgumentException("Invalid type " + setter);
		}
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T, P> ByteSetter<T> toByteSetter(final Setter<T, P> setter) {
		if (setter instanceof ByteSetter) {
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
	public <T, P> CharacterSetter<T> toCharacterSetter(final Setter<T, P> setter) {
		if (setter instanceof CharacterSetter) {
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
	public <T, P> ShortSetter<T> toShortSetter(final Setter<T, P> setter) {
		if (setter instanceof ShortSetter) {
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
	public <T, P> IntSetter<T> toIntSetter(final Setter<T, P> setter) {
		if (setter instanceof IntSetter) {
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
	public <T, P> LongSetter<T> toLongSetter(final Setter<T, P> setter) {
		if (setter instanceof LongSetter) {
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
	public <T, P> FloatSetter<T> toFloatSetter(final Setter<T, P> setter) {
		if (setter instanceof FloatSetter) {
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
	public <T, P> DoubleSetter<T> toDoubleSetter(final Setter<T, P> setter) {
		if (setter instanceof DoubleSetter) {
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
