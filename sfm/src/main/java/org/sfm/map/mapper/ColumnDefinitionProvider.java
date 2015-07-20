package org.sfm.map.mapper;


import org.sfm.map.FieldKey;

public interface ColumnDefinitionProvider<C extends ColumnDefinition<K, C>, K extends FieldKey<K>> {
    C getColumnDefinition(K key);
}
