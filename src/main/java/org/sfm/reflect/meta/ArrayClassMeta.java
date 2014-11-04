package org.sfm.reflect.meta;

import org.sfm.reflect.ReflectionService;

public class ArrayClassMeta<T> implements ClassMeta<T[]> {

	private final ReflectionService reflectionService;
	private final Class<T> elementTarget;
	private final ClassMeta<T> elementClassMeta;
	private Class<T[]> type;
	
	public ArrayClassMeta(Class<T[]> type, Class<T> elementTarget, ReflectionService reflectionService) {
		this.type = type;
		this.elementTarget = elementTarget;
		this.reflectionService = reflectionService;
		this.elementClassMeta = reflectionService.getClassMeta(elementTarget);
	}

	public ClassMeta<T> getElementClassMeta() {
		return elementClassMeta;
	}
	
	public Class<?> getElementTarget() {
		return elementTarget;
	}

	@Override
	public ReflectionService getReflectionService() {
		return reflectionService;
	}

	@Override
	public PropertyFinder<T[]> newPropertyFinder() {
		return new ArrayPropertyFinder<T>(this);
	}

	public Class<T[]> getType() {
		return type;
	}



}
