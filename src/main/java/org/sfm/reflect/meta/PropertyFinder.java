package org.sfm.reflect.meta;

import java.util.List;

import org.sfm.reflect.asm.ConstructorDefinition;
import org.sfm.utils.PropertyNameMatcher;

public interface PropertyFinder<T> {

	public PropertyMeta<T, ?> findProperty(
			PropertyNameMatcher propertyNameMatcher);

	public PropertyMeta<T, ?> findProperty(String propertyName);

	public List<ConstructorDefinition<T>> getEligibleConstructorDefinitions();

}