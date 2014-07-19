package org.flatmap.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.flatmap.reflect.primitive.BooleanFieldSetter;
import org.flatmap.reflect.primitive.BooleanMethodSetter;
import org.flatmap.reflect.primitive.BooleanSetter;
import org.flatmap.reflect.primitive.ByteFieldSetter;
import org.flatmap.reflect.primitive.ByteMethodSetter;
import org.flatmap.reflect.primitive.ByteSetter;
import org.flatmap.reflect.primitive.CharacterFieldSetter;
import org.flatmap.reflect.primitive.CharacterMethodSetter;
import org.flatmap.reflect.primitive.CharacterSetter;
import org.flatmap.reflect.primitive.DoubleFieldSetter;
import org.flatmap.reflect.primitive.DoubleMethodSetter;
import org.flatmap.reflect.primitive.DoubleSetter;
import org.flatmap.reflect.primitive.FloatFieldSetter;
import org.flatmap.reflect.primitive.FloatMethodSetter;
import org.flatmap.reflect.primitive.FloatSetter;
import org.flatmap.reflect.primitive.IntFieldSetter;
import org.flatmap.reflect.primitive.IntMethodSetter;
import org.flatmap.reflect.primitive.IntSetter;
import org.flatmap.reflect.primitive.LongFieldSetter;
import org.flatmap.reflect.primitive.LongMethodSetter;
import org.flatmap.reflect.primitive.LongSetter;
import org.flatmap.reflect.primitive.ShortFieldSetter;
import org.flatmap.reflect.primitive.ShortMethodSetter;
import org.flatmap.reflect.primitive.ShortSetter;

public class ReflectionSetterFactory implements SetterFactory {
	
	@Override
	public <T, P, C extends T> Setter<T, P> getSetter(Class<C> target, String property) {
		// first look for method
		Method method = lookForMethod(target, property);
		
		if (method == null) {
			// look for field
			Field field = lookForField(target, property);
			
			if (field != null) {
				field.setAccessible(true);
				return new FieldSetter<T, P>(field);
			}
		} else {
			return new MethodSetter<T, P>(method);
		}
		
		return null;
	}

	private Method lookForMethod(Class<?> target, String property) {
		
		for(Method m : target.getDeclaredMethods()) {
			if(methodModifiersMatches(m.getModifiers())
					&& methodNameMatchesProperty(m.getName(), property)) {
				return m;
			}
		}
		
		if (target.getSuperclass() != null) {
			return lookForMethod(target.getSuperclass(), property);
		}
		
		return null;
	}
	

	private Field lookForField(Class<?> target, String property) {
		for(Field field : target.getDeclaredFields()) {
			if(fieldModifiersMatches(field.getModifiers())
					&& fieldNameMatchesProperty(field.getName(), property)) {
				return field;
			}
		}
		
		if (target.getSuperclass() != null) {
			return lookForField(target.getSuperclass(), property);
		}
		
		return null;
	}
	
	private boolean methodModifiersMatches(int modifiers) {
		return !Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers);
	}

	private boolean methodNameMatchesProperty(String name, String property) {
		return (name.startsWith("set") && name.regionMatches(true, 3, property, 0, property.length())) 
				|| name.equalsIgnoreCase(property);
	}
	
	private boolean fieldModifiersMatches(int modifiers) {
		return !Modifier.isStatic(modifiers);
	}

	private boolean fieldNameMatchesProperty(String name, String property) {
		return  name.equalsIgnoreCase(property);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <T, P> BooleanSetter<T> toBooleanSetter(Setter<T, P> setter) {
		if (setter instanceof MethodSetter) {
			return new BooleanMethodSetter<T>(((MethodSetter) setter).getMethod());
		} else if (setter instanceof FieldSetter) {
			return new BooleanFieldSetter<T>(((FieldSetter) setter).getField());
		} else {
			throw new IllegalArgumentException("Invalid type " + setter);
		}
	}
	@SuppressWarnings("rawtypes")
	@Override
	public <T, P> ByteSetter<T> toByteSetter(Setter<T, P> setter) {
		if (setter instanceof MethodSetter) {
			return new ByteMethodSetter<T>(((MethodSetter) setter).getMethod());
		} else if (setter instanceof FieldSetter) {
			return new ByteFieldSetter<T>(((FieldSetter) setter).getField());
		} else {
			throw new IllegalArgumentException("Invalid type " + setter);
		}
	}
	@SuppressWarnings("rawtypes")
	@Override
	public <T, P> CharacterSetter<T> toCharacterSetter(Setter<T, P> setter) {
		if (setter instanceof MethodSetter) {
			return new CharacterMethodSetter<T>(((MethodSetter) setter).getMethod());
		} else if (setter instanceof FieldSetter) {
			return new CharacterFieldSetter<T>(((FieldSetter) setter).getField());
		} else {
			throw new IllegalArgumentException("Invalid type " + setter);
		}
	}
	@SuppressWarnings("rawtypes")
	@Override
	public <T, P> ShortSetter<T> toShortSetter(Setter<T, P> setter) {
		if (setter instanceof MethodSetter) {
			return new ShortMethodSetter<T>(((MethodSetter) setter).getMethod());
		} else if (setter instanceof FieldSetter) {
			return new ShortFieldSetter<T>(((FieldSetter) setter).getField());
		} else {
			throw new IllegalArgumentException("Invalid type " + setter);
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public <T, P> IntSetter<T> toIntSetter(Setter<T, P> setter) {
		if (setter instanceof MethodSetter) {
			return new IntMethodSetter<T>(((MethodSetter) setter).getMethod());
		} else if (setter instanceof FieldSetter) {
			return new IntFieldSetter<T>(((FieldSetter) setter).getField());
		} else {
			throw new IllegalArgumentException("Invalid type " + setter);
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public <T, P> LongSetter<T> toLongSetter(Setter<T, P> setter) {
		if (setter instanceof MethodSetter) {
			return new LongMethodSetter<T>(((MethodSetter) setter).getMethod());
		} else if (setter instanceof FieldSetter) {
			return new LongFieldSetter<T>(((FieldSetter) setter).getField());
		} else {
			throw new IllegalArgumentException("Invalid type " + setter);
		}
	}
	@SuppressWarnings("rawtypes")
	@Override
	public <T, P> FloatSetter<T> toFloatSetter(Setter<T, P> setter) {
		if (setter instanceof MethodSetter) {
			return new FloatMethodSetter<T>(((MethodSetter) setter).getMethod());
		} else if (setter instanceof FieldSetter) {
			return new FloatFieldSetter<T>(((FieldSetter) setter).getField());
		} else {
			throw new IllegalArgumentException("Invalid type " + setter);
		}
	}
	@SuppressWarnings("rawtypes")
	@Override
	public <T, P> DoubleSetter<T> toDoubleSetter(Setter<T, P> setter) {
		if (setter instanceof MethodSetter) {
			return new DoubleMethodSetter<T>(((MethodSetter) setter).getMethod());
		} else if (setter instanceof FieldSetter) {
			return new DoubleFieldSetter<T>(((FieldSetter) setter).getField());
		} else {
			throw new IllegalArgumentException("Invalid type " + setter);
		}
	}
}
