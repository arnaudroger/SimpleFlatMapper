package org.sfm.map.impl;

import org.sfm.reflect.meta.PropertyMeta;

public class PropertyMapping<T, P, K> {
	private final PropertyMeta<T, P> propertyMeta;
	private final K columnKey;
	
	
	public PropertyMapping(PropertyMeta<T, P> propertyMeta, K columnKey) {
		super();
		this.propertyMeta = propertyMeta;
		this.columnKey = columnKey;
	}

	public PropertyMeta<T, P> getPropertyMeta() {
		return propertyMeta;
	}

	public K getColumnKey() {
		return columnKey;
	}

	
}
