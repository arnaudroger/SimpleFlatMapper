package org.sfm.map.impl;

import org.sfm.map.ColumnDefinition;
import org.sfm.map.FieldKey;
import org.sfm.reflect.meta.PropertyMeta;

public class PropertyMapping<T, P, K extends FieldKey<K>, D extends ColumnDefinition<K, D>> {
	private final PropertyMeta<T, P> propertyMeta;
	private final K columnKey;
	private final D columnDefinition;

	public PropertyMapping(PropertyMeta<T, P> propertyMeta, K columnKey, D columnDefinition) {
		super();
		this.propertyMeta = propertyMeta;
		this.columnKey = columnKey;
		this.columnDefinition = columnDefinition;
	}

	public PropertyMeta<T, P> getPropertyMeta() {
		return propertyMeta;
	}

	public K getColumnKey() {
		return columnKey;
	}

	public D getColumnDefinition() {
		return columnDefinition;
	}


}
