package org.sfm.reflect.meta;

import java.util.List;

import org.sfm.reflect.asm.ConstructorDefinition;
import org.sfm.utils.PropertyNameMatcher;

public class ListPropertyFinder<T> implements PropertyFinder<List<T>> {

	private final ListClassMeta<T> listClassMeta;
	
	public ListPropertyFinder(ListClassMeta<T> listClassMeta) {
		this.listClassMeta = listClassMeta;
	}

	@Override
	public PropertyMeta<List<T>, ?> findProperty(
			PropertyNameMatcher propertyNameMatcher) {
		return null;
	}

	@Override
	public PropertyMeta<List<T>, ?> findProperty(String propertyName) {
		return findProperty(new PropertyNameMatcher(propertyName));
	}

	@Override
	public List<ConstructorDefinition<List<T>>> getEligibleConstructorDefinitions() {
		throw new UnsupportedOperationException();
	}

}
