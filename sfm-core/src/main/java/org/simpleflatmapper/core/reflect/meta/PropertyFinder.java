package org.simpleflatmapper.core.reflect.meta;

import org.simpleflatmapper.core.reflect.InstantiatorDefinition;

import java.util.List;

public interface PropertyFinder<T> {
	<E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher);
	List<InstantiatorDefinition> getEligibleInstantiatorDefinitions();

    PropertyFinder<?> getSubPropertyFinder(String name);
}