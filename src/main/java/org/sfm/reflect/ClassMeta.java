package org.sfm.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sfm.map.MapperBuildingException;
import org.sfm.reflect.asm.ConstructorDefinition;
import org.sfm.utils.PropertyNameMatcher;

public class ClassMeta<T> {
	
	private final List<PropertyMeta<T, ?>> properties;
	
	private final List<ConstructorDefinition<T>> constructorDefinitions;
	
	public ClassMeta(Class<T> target, SetterFactory setterFactory, boolean asmPresent) throws MapperBuildingException {
		if (asmPresent) {
			try {
				this.constructorDefinitions = ConstructorDefinition.extractConstructors(target);
			} catch(Exception e) {
				throw new MapperBuildingException(e.getMessage(), e);
			}
		} else {
			this.constructorDefinitions = null;
		}
		this.properties = listProperties(setterFactory, target);
	}

	private List<PropertyMeta<T, ?>> listProperties(SetterFactory setterFactory, Class<?> target) {
		final List<PropertyMeta<T, ?>> properties = new ArrayList<>();
		final Set<String> propertiesSet = new HashSet<String>();
		Class<?> currentClass = target;
		
		while(!Object.class.equals(currentClass)) {
			
			for(Method method : currentClass.getDeclaredMethods()) {
				final String name = method.getName();
				if (SetterHelper.methodModifiersMatches(method.getModifiers()) && SetterHelper.isSetter(name)) {
					final String propertyName = name.substring(3,4).toLowerCase() +  name.substring(4);
					if (!propertiesSet.contains(propertyName)) {
						properties.add(new MethodPropertyMeta<T, Object>(propertyName, method, setterFactory));
						propertiesSet.add(propertyName);
					}
				}
			}
			
			for(Field field : currentClass.getDeclaredFields()) {
				final String name = field.getName();
				if (SetterHelper.fieldModifiersMatches(field.getModifiers())) {
					if (!propertiesSet.contains(name)) {
						properties.add(new FieldPropertyMeta<T, Object>(field));
						propertiesSet.add(name);
					}
				}
			}
			
			currentClass = currentClass.getSuperclass();
		}
		
		return properties;
	}

	public Setter<T, ?> findSetter(final PropertyNameMatcher propertyNameMatcher) {
		for (PropertyMeta<T, ?> prop : properties) {
			if (propertyNameMatcher.matches(prop.getName())) {
				return prop.getSetter();
			}
		}
		return null;
	}

	public final Setter<T, ?> findSetter(final String column) {
		return findSetter(new PropertyNameMatcher(column));
	}

	public List<ConstructorDefinition<T>> getConstructorDefinitions() {
		return constructorDefinitions;
	}
}
