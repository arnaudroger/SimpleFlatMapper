package org.sfm.reflect.meta;

import org.sfm.jdbc.MapperBuilder;

public class SubProperty<S, T, K> {
	private final MapperBuilder<S, T, K, ?, ?> mapperBuilder;
	private final SubPropertyMeta<T, ?> subProperty;

	public SubProperty(MapperBuilder<S, T, K, ?, ?> mapperBuilder,
			SubPropertyMeta<T, ?> subProperty) {
		this.mapperBuilder = mapperBuilder;
		this.subProperty = subProperty;
	}

	public MapperBuilder<S, T, K, ?, ?> getMapperBuilder() {
		return mapperBuilder;
	}

	public SubPropertyMeta<T, ?> getSubProperty() {
		return subProperty;
	}

}