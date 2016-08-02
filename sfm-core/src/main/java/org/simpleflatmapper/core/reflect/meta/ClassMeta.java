package org.simpleflatmapper.core.reflect.meta;

import org.simpleflatmapper.core.reflect.InstantiatorDefinition;
import org.simpleflatmapper.core.reflect.ReflectionService;

import java.lang.reflect.Type;
import java.util.List;

public interface ClassMeta<T> {

	ReflectionService getReflectionService();

	PropertyFinder<T> newPropertyFinder();

	Type getType();

	String[] generateHeaders();

	boolean isLeaf();

	List<InstantiatorDefinition> getInstantiatorDefinitions();
}