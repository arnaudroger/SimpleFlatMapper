package org.sfm.reflect.meta;

import org.sfm.reflect.ConstructorDefinition;

import java.util.List;

public interface PropertyFinder<T> {

	public <E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher);

	public List<ConstructorDefinition<T>> getEligibleConstructorDefinitions();

    public <E> ConstructorPropertyMeta<T,E> findConstructor(ConstructorDefinition<T> constructorDefinition);
}