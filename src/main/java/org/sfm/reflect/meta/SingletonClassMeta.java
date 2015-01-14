package org.sfm.reflect.meta;

import org.sfm.map.MapperBuildingException;
import org.sfm.reflect.ConstructorDefinition;
import org.sfm.reflect.ReflectionService;

import java.lang.reflect.Type;
import java.util.List;

public class SingletonClassMeta<T> implements ClassMeta<T> {

	private final ClassMeta<T> classMeta;

	public SingletonClassMeta(ClassMeta<T> classMeta) {
		this.classMeta = classMeta;
	}


	@Override
	public ReflectionService getReflectionService() {
		return classMeta.getReflectionService();
	}

	@Override
	public PropertyFinder<T> newPropertyFinder() {
		return new SingletonPropertyFinder<T>(classMeta);
	}

	@Override
	public Type getType() {
		return classMeta.getType();
	}

}
