package org.sfm.reflect.meta;

import java.lang.reflect.Type;
import java.util.List;

import org.sfm.reflect.ReflectionService;

public class ListClassMeta<T> implements ClassMeta<List<T>> {

	private final ReflectionService reflectionService;
	private final Type elementTarget;
	private final String prefix;
	private final ClassMeta<T> elementClassMeta;
	
	public ListClassMeta(String prefix, Type elementTarget, ReflectionService reflectionService) {
		this.elementTarget = elementTarget;
		this.reflectionService = reflectionService;
		this.prefix = prefix;
		this.elementClassMeta = reflectionService.getClassMeta(prefix, elementTarget);
	}

	public ClassMeta<T> getElementClassMeta() {
		return elementClassMeta;
	}
	
	public Type getElementTarget() {
		return elementTarget;
	}

	public String getPrefix() {
		return prefix;
	}

	@Override
	public ReflectionService getReflectionService() {
		return reflectionService;
	}

	@Override
	public PropertyFinder<List<T>> newPropertyFinder() {
		return new ListPropertyFinder<T>(this);
	}



}
