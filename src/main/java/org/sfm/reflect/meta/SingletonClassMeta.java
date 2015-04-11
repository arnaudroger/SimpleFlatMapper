package org.sfm.reflect.meta;

import org.sfm.reflect.InstantiatorDefinition;
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

	@Override
	public String[] generateHeaders() {
		return classMeta.generateHeaders();
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public List<InstantiatorDefinition> getInstantiatorDefinitions() {
		return classMeta.getInstantiatorDefinitions();
	}

}
