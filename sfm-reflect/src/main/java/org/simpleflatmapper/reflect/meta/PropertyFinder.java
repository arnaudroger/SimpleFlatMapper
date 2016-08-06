package org.simpleflatmapper.reflect.meta;

import org.simpleflatmapper.reflect.InstantiatorDefinition;

import java.util.List;

public interface PropertyFinder<T> {
	<E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher);
	List<InstantiatorDefinition> getEligibleInstantiatorDefinitions();

    PropertyFinder<?> getSubPropertyFinder(String name);
}