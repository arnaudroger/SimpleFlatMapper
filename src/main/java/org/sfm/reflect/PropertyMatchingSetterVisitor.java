package org.sfm.reflect;

import org.sfm.utils.PropertyNameMatcher;

public final class PropertyMatchingSetterVisitor<T> implements SetterVisitor<T>{

	private final PropertyNameMatcher nameMatcher;
	private Setter<T, Object> matchedSetter;
	
	public PropertyMatchingSetterVisitor(final PropertyNameMatcher nameMatcher) {
		this.nameMatcher = nameMatcher;
	}

	@Override
	public boolean visitSetter(final String property, final Setter<T, Object> setter) {
		if (nameMatcher.matches(property)) {
			matchedSetter = setter;
			return false;
		}
		return true;
	}
	
	public Setter<T, Object> setter() {
		return matchedSetter;
	}
	
}
