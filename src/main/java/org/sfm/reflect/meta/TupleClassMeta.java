package org.sfm.reflect.meta;

import org.sfm.map.MapperBuildingException;
import org.sfm.reflect.ConstructorDefinition;
import org.sfm.reflect.ReflectionService;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class TupleClassMeta<T> implements ClassMeta<T> {

	private final ReflectionService reflectionService;
	private Type type;
	private List<ConstructorDefinition<T>> constructorDefinitions;

	public TupleClassMeta(Type type, ReflectionService reflectionService) {
		this.type = type;
		this.reflectionService = reflectionService;

		try {
			this.constructorDefinitions = reflectionService.extractConstructors(type);
		} catch(Exception e) {
			throw new MapperBuildingException(e.getMessage(), e);
		}
	}


	@Override
	public ReflectionService getReflectionService() {
		return reflectionService;
	}

	@Override
	public PropertyFinder<T> newPropertyFinder() {
		return new TuplePropertyFinder<T>(this);
	}

	public Type getType() {
		return type;
	}


	public List<ConstructorDefinition<T>> getConstructorDefinitions() {
		return constructorDefinitions;
	}

	public int getTupleSize() {
		return constructorDefinitions.get(0).getParameters().length;
	}
}
