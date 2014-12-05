package org.sfm.reflect.meta;

import java.util.List;

import org.sfm.reflect.ConstructorDefinition;

public interface PropertyFinder<T> {

	public <E> PropertyMeta<T, E> findProperty(PropertyNameMatcher propertyNameMatcher);

	public <E> PropertyMeta<T, E> findProperty(String propertyName);

	public List<ConstructorDefinition<T>> getEligibleConstructorDefinitions();

	public Class<?> getClassToInstantiate();

}