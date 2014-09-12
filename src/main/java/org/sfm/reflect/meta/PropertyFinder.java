package org.sfm.reflect.meta;

import java.util.List;

import org.sfm.reflect.asm.ConstructorDefinition;

public interface PropertyFinder<T> {

	public PropertyMeta<T, ?> findProperty(PropertyNameMatcher propertyNameMatcher);

	public PropertyMeta<T, ?> findProperty(String propertyName);

	public List<ConstructorDefinition<T>> getEligibleConstructorDefinitions();

	public Class<? extends T> getClassToInstantiate();

}