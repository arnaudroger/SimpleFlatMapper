package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import static org.simpleflatmapper.util.Asserts.requireNonNull;

public class PropertyMapping<T, P, K extends FieldKey<K>> {
	private final PropertyMeta<T, P> propertyMeta;
	private final K columnKey;
	private final ColumnDefinition<K, ?> columnDefinition;

	public PropertyMapping(PropertyMeta<T, P> propertyMeta, K columnKey, ColumnDefinition<K, ?> columnDefinition) {
		super();
		this.propertyMeta = requireNonNull("propertyMeta", propertyMeta);
		this.columnKey = requireNonNull("propertyMeta", columnKey);
		this.columnDefinition = requireNonNull("propertyMeta", columnDefinition);
	}

	public <TT, PP> PropertyMapping<TT, PP, K> propertyMeta(PropertyMeta<TT, PP> propertyMeta) {
		Object[] definedProperties = propertyMeta.getDefinedProperties();
		ColumnDefinition<K, ?> mergeColumnDefintion = definedProperties != null ?  this.columnDefinition.newColumnDefinition(definedProperties) : this.columnDefinition;
		return new PropertyMapping<TT, PP, K>(propertyMeta, columnKey, mergeColumnDefintion);
	}

	public PropertyMeta<T, P> getPropertyMeta() {
		return propertyMeta;
	}

	public K getColumnKey() {
		return columnKey;
	}

	public ColumnDefinition<K, ?> getColumnDefinition() {
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

	public PropertyMapping<T, P, K> toNonMapped() {
		return new PropertyMapping<T, P, K>(propertyMeta.toNonMapped(), columnKey, columnDefinition);

	}

	public PropertyMapping<T, ?, K> compressSubSelf() {
		return new PropertyMapping<T, P, K>(propertyMeta.compressSubSelf(), columnKey, columnDefinition);
	}
}
