package org.sfm.reflect.meta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sfm.map.MapperBuildingException;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.SetterHelper;
import org.sfm.reflect.TypeHelper;
import org.sfm.reflect.ConstructorDefinition;
import org.sfm.reflect.ConstructorParameter;

public final class ObjectClassMeta<T> implements ClassMeta<T> {
	
	private final List<PropertyMeta<T, ?>> properties;
	private final List<ConstructorPropertyMeta<T, ?>> constructorProperties;
	private final List<ConstructorDefinition<T>> constructorDefinitions;

	
	private final ReflectionService reflectService;
	private final Type target;

	private final Map<String, String> fieldAliases;
	
	public ObjectClassMeta(Type target, ReflectionService reflectService) throws MapperBuildingException {
		this.reflectService = reflectService;
		try {
			this.constructorDefinitions = reflectService.extractConstructors(target);
			this.constructorProperties = Collections.unmodifiableList(listProperties(constructorDefinitions));
		} catch(Exception e) {
			throw new MapperBuildingException(e.getMessage(), e);
		}
		this.fieldAliases = Collections.unmodifiableMap(aliases(reflectService, TypeHelper.<T>toClass(target)));
		this.properties = Collections.unmodifiableList(listProperties(reflectService, TypeHelper.<T>toClass(target)));
		this.target = target;
	}
	
	private Map<String, String> aliases(final ReflectionService reflectService, Class<T> target) {
		final Map<String, String> map = new HashMap<String, String>();
		
		ClassVisitor.visit(target, new FieldAndMethodCallBack() {
			@Override
			public void method(Method method) {
				String alias = reflectService.getColumnName(method);
				if (alias != null) {
					map.put(SetterHelper.getPropertyNameFromMethodName(method.getName()), alias);
				}
			}
			
			@Override
			public void field(Field field) {
				String alias = reflectService.getColumnName(field);
				if (alias != null) {
					map.put(field.getName(), alias);
				}
			}
		});
		
		return map;
	}

	private List<ConstructorPropertyMeta<T, ?>> listProperties(List<ConstructorDefinition<T>> constructorDefinitions) {
		if (constructorDefinitions == null) return null;
		
		Set<String> properties = new HashSet<String>();
		List<ConstructorPropertyMeta<T, ?>> constructorProperties = new ArrayList<ConstructorPropertyMeta<T, ?>>();
		for(ConstructorDefinition<T> cd : constructorDefinitions) {
			for(ConstructorParameter param : cd.getParameters()) {
				String paramName = param.getName();
				if (!properties.contains(paramName)) {
					constructorProperties.add(new ConstructorPropertyMeta<T, Object>(paramName, paramName, reflectService, param));
					properties.add(paramName);
				}
			}
		}
		return constructorProperties;
	}

	private List<PropertyMeta<T, ?>> listProperties(final ReflectionService reflectService, Class<?> target) {
		final List<PropertyMeta<T, ?>> properties = new ArrayList<PropertyMeta<T, ?>>();
		final Set<String> propertiesSet = new HashSet<String>();
		
		ClassVisitor.visit(target, new FieldAndMethodCallBack() {
			@Override
			public void method(Method method) {
				final String name = method.getName();
				if (SetterHelper.methodModifiersMatches(method.getModifiers()) && SetterHelper.isSetter(name)) {
					final String propertyName = SetterHelper.getPropertyNameFromMethodName(name);
					if (!propertiesSet.contains(propertyName)) {
						properties.add(new MethodPropertyMeta<T, Object>(propertyName, getAlias(propertyName), reflectService, method));
						propertiesSet.add(propertyName);
					}
				}
			}
			
			@Override
			public void field(Field field) {
				final String name = field.getName();
				if (SetterHelper.fieldModifiersMatches(field.getModifiers())) {
					if (!propertiesSet.contains(name)) {
						properties.add(new FieldPropertyMeta<T, Object>(field.getName(), getAlias(name), reflectService, field));
						propertiesSet.add(name);
					}
				}
			}
		});
		
		return properties;
	}


	private String getAlias(String propertyName) {
		String columnName = this.fieldAliases.get(propertyName);
		if (columnName == null) {
			columnName = propertyName;
		}
		return columnName;
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

	@Override
	public ReflectionService getReflectionService() {
		return reflectService;
	}

	@Override
	public PropertyFinder<T> newPropertyFinder() {
		return new ObjectPropertyFinder<T>(this);
	}

	public Type getTargetClass() {
		return target;
	}
}
