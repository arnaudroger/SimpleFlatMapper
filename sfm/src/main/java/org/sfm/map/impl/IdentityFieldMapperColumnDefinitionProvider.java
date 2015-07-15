package org.sfm.map.impl;


import org.sfm.map.ColumnDefinitionProvider;
import org.sfm.map.FieldKey;

public class IdentityFieldMapperColumnDefinitionProvider<K extends FieldKey<K>, S> implements ColumnDefinitionProvider<FieldMapperColumnDefinition<K, S>, K> {
    @Override
    public FieldMapperColumnDefinition<K, S> getColumnDefinition(K key) {
        return FieldMapperColumnDefinition.<K, S>identity();
    }
}
