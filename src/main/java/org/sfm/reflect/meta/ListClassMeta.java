package org.sfm.reflect.meta;

import java.util.List;

import org.sfm.reflect.ReflectionService;

public class ListClassMeta<T> implements ClassMeta<List<T>> {

	private final ReflectionService reflectionService;
	private final Class<?> elementTarget;
	private final String prefix;
	
	public ListClassMeta(String prefix, Class<?> target, ReflectionService reflectionService) {
		this.elementTarget = target.getTypeParameters()[0].getClass();
		this.reflectionService = reflectionService;
		this.prefix = prefix;
	}

	@Override
	public ReflectionService getReflectionService() {
		return reflectionService;
	}

	@Override
	public PropertyFinder<List<T>> newPropertyFinder() {
		return new ListPropertyFinder(this);
	}

}
