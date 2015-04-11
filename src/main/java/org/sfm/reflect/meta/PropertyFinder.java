package org.sfm.reflect.meta;

import org.sfm.reflect.InstantiatorDefinition;

import java.util.List;

public interface PropertyFinder<T> {

	<E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher);

	List<InstantiatorDefinition> getEligibleInstantiatorDefinitions();

    <E> ConstructorPropertyMeta<T,E> findConstructor(InstantiatorDefinition instantiatorDefinition);
}