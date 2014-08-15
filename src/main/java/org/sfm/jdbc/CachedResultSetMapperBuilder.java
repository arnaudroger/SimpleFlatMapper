package org.sfm.jdbc;

import java.util.Map;
import java.util.Map.Entry;

import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;
import org.sfm.utils.PropertyNameMatcher;

public class CachedResultSetMapperBuilder<T> extends AbstractResultSetMapperBuilder<T> {

	private final Map<String, Setter<T, Object>> setters;
	

	public CachedResultSetMapperBuilder(Class<T> target,
			Map<String, Setter<T, Object>> setters, SetterFactory setterFactory) {
		super(target, setterFactory);
		this.setters = setters;
	}


	protected Setter<T, Object> findSetter(PropertyNameMatcher propertyNameMatcher) {
		for (Entry<String, Setter<T, Object>> e : setters.entrySet()) {
			if (propertyNameMatcher.matches(e.getKey())) {
				return e.getValue();
			}
		}
		return null;
	}


	@Override
	protected Setter<T, Object> findSetter(String column) {
		return findSetter(new PropertyNameMatcher(column));
	}


	@Override
	protected Setter<T, Object> getSetter(String property) {
		return setters.get(property);
	}

}
