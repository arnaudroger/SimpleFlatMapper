package org.sfm.reflect;

import org.sfm.utils.PropertyNameMatcher;

public class PropertyMatchingSetterVisitor<T> implements SetterVisitor<T>{

	private final PropertyNameMatcher nameMatcher;
	private Setter<T, Object> matchedSetter;
	
	public PropertyMatchingSetterVisitor(PropertyNameMatcher nameMatcher) {
		this.nameMatcher = nameMatcher;
	}

	@Override
	public boolean visitSetter(String property, Setter<T, Object> setter) {
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
