package org.sfm.jdbc;

import java.util.Map;
import java.util.Map.Entry;

import org.sfm.map.MapperBuildingException;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;
import org.sfm.utils.PropertyNameMatcher;

public final class CachedResultSetMapperBuilder<T> extends AbstractResultSetMapperBuilder<T> {

	private final Map<String, Setter<T, Object>> setters;

	public CachedResultSetMapperBuilder(final Class<T> target, final Map<String, Setter<T, Object>> setters, final SetterFactory setterFactory, final boolean asmPresent) throws MapperBuildingException {
		super(target, setterFactory, asmPresent);
		this.setters = setters;
	}

	private Setter<T, Object> findSetter(final PropertyNameMatcher propertyNameMatcher) {
		for (Entry<String, Setter<T, Object>> e : setters.entrySet()) {
			if (propertyNameMatcher.matches(e.getKey())) {
				return e.getValue();
			}
		}
		return null;
	}

	@Override
	protected final Setter<T, Object> findSetter(final String column) {
		return findSetter(new PropertyNameMatcher(column));
	}

	@Override
	protected final Setter<T, Object> getSetter(final String property) {
		return setters.get(property);
	}
}
