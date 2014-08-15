package org.sfm.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import org.sfm.utils.PropertyNameMatcher;

public class SetterFactory {
	
	private final AsmFactory asmFactory;
	
	public SetterFactory(AsmFactory asmSetterFactory) {
		this.asmFactory = asmSetterFactory;
	}
	
	public SetterFactory() {
		this.asmFactory = new AsmFactory();
	}

	public AsmFactory getAsmFactory() {
		return asmFactory;
	}

	public <T, P, C extends T> Setter<T, P> getSetter(Class<C> target, String property) {
		// first look for method
		Method method = lookForMethod(target, property);
		
		if (method == null) {
			return getFieldSetter(target, property);
		} else {
			return getMethodSetter(method);
		}
	}

	private <T, P, C extends T> Setter<T, P> getMethodSetter(Method method) {
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

	public <T, P, C extends T> FieldSetter<T, P> getFieldSetter(Class<C> target, String property) {
		// look for field
		Field field = lookForField(target, property);
		
		if (field != null) {
			field.setAccessible(true);
			return new FieldSetter<T, P>(field);
		} else {
			return null;
		}
	}

	private Method lookForMethod(Class<?> target, String property) {
		
		for(Method m : target.getDeclaredMethods()) {
			if(methodModifiersMatches(m.getModifiers())
					&& methodNameMatchesProperty(m.getName(), property)) {
				return m;
			}
		}
		
		if (!Object.class.equals(target)) {
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
		
		if (!Object.class.equals(target)) {
			return lookForField(target.getSuperclass(), property);
		}
		
		return null;
	}
	
	private boolean methodModifiersMatches(int modifiers) {
		return !Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers);
	}

	private boolean methodNameMatchesProperty(String name, String property) {
		return (isSetter(name) && name.regionMatches(true, 3, property, 0, property.length())) 
				|| name.equalsIgnoreCase(property);
	}
	
	private boolean fieldModifiersMatches(int modifiers) {
		return !Modifier.isStatic(modifiers);
	}

	private boolean fieldNameMatchesProperty(String name, String property) {
		return  name.equalsIgnoreCase(property);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T, P> BooleanSetter<T> toBooleanSetter(Setter<T, P> setter) {
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
	public <T, P> ByteSetter<T> toByteSetter(Setter<T, P> setter) {
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
	public <T, P> CharacterSetter<T> toCharacterSetter(Setter<T, P> setter) {
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
	public <T, P> ShortSetter<T> toShortSetter(Setter<T, P> setter) {
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
	public <T, P> IntSetter<T> toIntSetter(Setter<T, P> setter) {
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
	public <T, P> LongSetter<T> toLongSetter(Setter<T, P> setter) {
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
	public <T, P> FloatSetter<T> toFloatSetter(Setter<T, P> setter) {
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
	public <T, P> DoubleSetter<T> toDoubleSetter(Setter<T, P> setter) {
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

	public <T> Map<String, Setter<T, Object>> getAllSetters(Class<T> target) {
		final Map<String, Setter<T, Object>> setters = new HashMap<String, Setter<T,Object>>();
		
		visitSetters(new SetterVisitor<T>() {
			@Override
			public boolean visitSetter(String property, Setter<T, Object> setter) {
				setters.put(property, setter);
				return true;
			}
		}, target);
		
		return setters;
	}
	
	public <T> Setter<T, Object> findSetter(PropertyNameMatcher matcher, Class<T> target) {
		return visitSetters(new PropertyMatchingSetterVisitor<T>(matcher), target).setter();
	}
	
	public <T, P, C extends SetterVisitor<T>> C visitSetters(C visitor, Class<T> target) {
		Set<String> properties = new HashSet<String>();
		Class<?> currentClass = target;
		
		while(!Object.class.equals(currentClass)) {
			
			for(Method method : currentClass.getDeclaredMethods()) {
				String name = method.getName();
				if (methodModifiersMatches(method.getModifiers()) && isSetter(name)) {
					String propertyName = name.substring(3,4).toLowerCase() +  name.substring(4);
					if (!properties.contains(propertyName)) {
						Setter<T, Object> setter = getMethodSetter(method);
						if (!visitor.visitSetter(propertyName, setter)) {
							return visitor;
						}
						properties.add(propertyName);
					}
				}
			}
			
			for(Field field : currentClass.getDeclaredFields()) {
				String name = field.getName();
				if (fieldModifiersMatches(field.getModifiers())) {
					if (!properties.contains(name)) {
						field.setAccessible(true);
						Setter<T, Object> setter = new FieldSetter<T, Object>(field);
						if (!visitor.visitSetter(name, setter)) {
							return visitor;
						}
						properties.add(name);
					}
				}
			}
			
			currentClass = currentClass.getSuperclass();
		}
		
		return visitor;
	}

	private boolean isSetter(String name) {
		return name.length() > 3 && name.startsWith("set");
	}
}
