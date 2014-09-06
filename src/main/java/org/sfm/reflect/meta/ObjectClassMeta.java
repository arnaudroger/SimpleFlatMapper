package org.sfm.reflect.meta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sfm.map.MapperBuildingException;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.SetterHelper;
import org.sfm.reflect.asm.ConstructorDefinition;
import org.sfm.reflect.asm.ConstructorParameter;

public final class ObjectClassMeta<T> implements ClassMeta<T> {
	
	private final List<PropertyMeta<T, ?>> properties;
	private final List<ConstructorPropertyMeta<T, ?>> constructorProperties;
	private final List<ConstructorDefinition<T>> constructorDefinitions;

	private final String prefix;
	
	private final ReflectionService reflectService;

	public ObjectClassMeta(Class<T> target, ReflectionService reflectService) throws MapperBuildingException {
		this(null, target, reflectService);
	}
	
	public ObjectClassMeta(String prefix, Class<T> target, ReflectionService reflectService) throws MapperBuildingException {
		this.prefix = prefix;
		this.reflectService = reflectService;
		if (reflectService.isAsmPresent()) {
			try {
				this.constructorDefinitions = ConstructorDefinition.extractConstructors(target);
				this.constructorProperties = Collections.unmodifiableList(listProperties(constructorDefinitions));
			} catch(Exception e) {
				throw new MapperBuildingException(e.getMessage(), e);
			}
		} else {
			this.constructorDefinitions = null;
			this.constructorProperties = null;
		}
		this.properties = Collections.unmodifiableList(listProperties(reflectService, target));
	}
	
	private List<ConstructorPropertyMeta<T, ?>> listProperties(List<ConstructorDefinition<T>> constructorDefinitions) {
		if (constructorDefinitions == null) return null;
		
		Set<String> properties = new HashSet<>();
		List<ConstructorPropertyMeta<T, ?>> constructorProperties = new ArrayList<>();
		for(ConstructorDefinition<T> cd : constructorDefinitions) {
			for(ConstructorParameter param : cd.getParameters()) {
				String paramName = param.getName();
				if (!properties.contains(paramName)) {
					constructorProperties.add(new ConstructorPropertyMeta<T, Object>(addPrefix(paramName), reflectService, param));
					properties.add(paramName);
				}
			}
		}
		return constructorProperties;
	}

	private List<PropertyMeta<T, ?>> listProperties(ReflectionService reflectService, Class<?> target) {
		final List<PropertyMeta<T, ?>> properties = new ArrayList<>();
		final Set<String> propertiesSet = new HashSet<String>();
		Class<?> currentClass = target;
		
		while(currentClass != null && !Object.class.equals(currentClass)) {
			
			for(Method method : currentClass.getDeclaredMethods()) {
				final String name = method.getName();
				if (SetterHelper.methodModifiersMatches(method.getModifiers()) && SetterHelper.isSetter(name)) {
					final String propertyName = name.substring(3,4).toLowerCase() +  name.substring(4);
					if (!propertiesSet.contains(propertyName)) {
						properties.add(new MethodPropertyMeta<T, Object>(addPrefix(propertyName), reflectService, method));
						propertiesSet.add(propertyName);
					}
				}
			}
			
			for(Field field : currentClass.getDeclaredFields()) {
				final String name = field.getName();
				if (SetterHelper.fieldModifiersMatches(field.getModifiers())) {
					if (!propertiesSet.contains(name)) {
						properties.add(new FieldPropertyMeta<T, Object>(addPrefix(field.getName()), reflectService, field));
						propertiesSet.add(name);
					}
				}
			}
			
			currentClass = currentClass.getSuperclass();
		}
		
		return properties;
	}

	private String addPrefix(String propertyName) {
		return prefix == null ? propertyName : prefix + propertyName;
	}

	List<ConstructorDefinition<T>> getConstructorDefinitions() {
		return constructorDefinitions;
	}

	List<PropertyMeta<T, ?>> getProperties() {
		return properties;
	}

	List<ConstructorPropertyMeta<T, ?>> getConstructorProperties() {
		return constructorProperties;
	}

	/* (non-Javadoc)
	 * @see org.sfm.reflect.meta.ClassMeta#getReflectionService()
	 */
	@Override
	public ReflectionService getReflectionService() {
		return reflectService;
	}

	/* (non-Javadoc)
	 * @see org.sfm.reflect.meta.ClassMeta#newPropertyFinder()
	 */
	@Override
	public PropertyFinder<T> newPropertyFinder() {
		return new ObjectPropertyFinder<>(this);
	}
}
