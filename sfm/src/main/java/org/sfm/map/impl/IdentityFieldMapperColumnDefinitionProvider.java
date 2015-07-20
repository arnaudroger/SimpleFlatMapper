package org.sfm.map.impl;


import org.sfm.map.mapper.ColumnDefinitionProvider;
import org.sfm.map.FieldKey;
import org.sfm.map.column.FieldMapperColumnDefinition;

public class IdentityFieldMapperColumnDefinitionProvider<K extends FieldKey<K>, S> implements ColumnDefinitionProvider<FieldMapperColumnDefinition<K, S>, K> {
    @Override
    public FieldMapperColumnDefinition<K, S> getColumnDefinition(K key) {
        return FieldMapperColumnDefinition.<K, S>identity();
    }
}
