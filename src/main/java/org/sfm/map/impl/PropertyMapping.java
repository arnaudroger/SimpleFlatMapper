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

    @Override
    public String toString() {
        return "PropertyMapping{" +
                "propertyMeta=" + propertyMeta +
                ", columnKey=" + columnKey +
                ", columnDefinition=" + columnDefinition +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PropertyMapping that = (PropertyMapping) o;

        if (columnDefinition != null ? !columnDefinition.equals(that.columnDefinition) : that.columnDefinition != null)
            return false;
        if (columnKey != null ? !columnKey.equals(that.columnKey) : that.columnKey != null) return false;
        if (propertyMeta != null ? !propertyMeta.equals(that.propertyMeta) : that.propertyMeta != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = propertyMeta != null ? propertyMeta.hashCode() : 0;
        result = 31 * result + (columnKey != null ? columnKey.hashCode() : 0);
        result = 31 * result + (columnDefinition != null ? columnDefinition.hashCode() : 0);
        return result;
    }
}
