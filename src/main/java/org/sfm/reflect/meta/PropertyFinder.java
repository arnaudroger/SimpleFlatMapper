package org.sfm.reflect.meta;

import org.sfm.reflect.InstantiatorDefinition;

import java.util.List;

public interface PropertyFinder<T> {

	public <E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher);

	public List<InstantiatorDefinition> getEligibleInstantiatorDefinitions();

    public <E> ConstructorPropertyMeta<T,E> findConstructor(InstantiatorDefinition instantiatorDefinition);
}