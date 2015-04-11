package org.sfm.reflect.meta;

import org.sfm.reflect.InstantiatorDefinition;
import org.sfm.reflect.ReflectionService;

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