package org.atclements.setter.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.atclements.setter.Setter;

public class ReflectionSetterFactory {
	public Setter getSetter(Class<?> target, String property) {
		// first look for method
		Method method = lookForMethod(target, property);
		
		if (method == null) {
			// look for field
			Field field = lookForField(target, property);
			
			if (field != null) {
				field.setAccessible(true);
				return new FieldSetter(field);
			}
		} else {
			return new MethodSetter(method);
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
}
