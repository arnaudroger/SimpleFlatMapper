package org.sfm.reflect;

import java.util.Map;
import java.util.Map.Entry;

import org.sfm.utils.PropertyNameMatcher;

public class ClassMeta<T> {
	private final Map<String, Setter<T, ?>> setters;
	
	public ClassMeta(Map<String, Setter<T, ?>> setters) {
		super();
		this.setters = setters;
	}

	public Setter<T, ?> findSetter(final PropertyNameMatcher propertyNameMatcher) {
		for (Entry<String, Setter<T, ?>> e : setters.entrySet()) {
			if (propertyNameMatcher.matches(e.getKey())) {
				return e.getValue();
			}
		}
		return null;
	}

	public final Setter<T, ?> findSetter(final String column) {
		return findSetter(new PropertyNameMatcher(column));
	}

	public final Setter<T, ?> getSetter(final String property) {
		return setters.get(property);
	}
	
}
