package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.ReflectionService;

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