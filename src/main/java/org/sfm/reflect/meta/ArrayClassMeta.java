package org.sfm.reflect.meta;

import org.sfm.reflect.InstantiatorDefinition;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class ArrayClassMeta<T, E> implements ClassMeta<T> {

	private final ReflectionService reflectionService;
	private final Type elementTarget;
	private final ClassMeta<E> elementClassMeta;
	private final Type type;

	public ArrayClassMeta(Type type, Type elementTarget, ReflectionService reflectionService) {
		this.type = type;
		this.elementTarget = elementTarget;
		this.reflectionService = reflectionService;
		this.elementClassMeta = reflectionService.getClassMeta(elementTarget);
	}

	public ClassMeta<E> getElementClassMeta() {
		return elementClassMeta;
	}
	
	public Type getElementTarget() {
		return elementTarget;
	}

	@Override
	public ReflectionService getReflectionService() {
		return reflectionService;
	}

	@Override
	public PropertyFinder<T> newPropertyFinder() {
		return new ArrayPropertyFinder<T, E>(this);
	}

	public Type getType() {
		return type;
	}

	@Override
	public String[] generateHeaders() {
		throw new UnsupportedOperationException("Cannot generate headers for a list/array");
	}

    public boolean isArray() {
        return TypeHelper.isArray(type);
    }

	@Override
	public boolean isLeaf() {
		return false;
	}

	@Override
	public List<InstantiatorDefinition> getInstantiatorDefinitions() {
		return Collections.emptyList();
	}
}
