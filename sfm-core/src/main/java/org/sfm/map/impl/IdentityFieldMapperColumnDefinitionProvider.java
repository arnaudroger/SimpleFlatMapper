package org.sfm.map.impl;


import org.sfm.map.column.ColumnProperty;
import org.sfm.map.mapper.ColumnDefinitionProvider;
import org.sfm.map.FieldKey;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.utils.BiConsumer;
import org.sfm.utils.Predicate;

public class IdentityFieldMapperColumnDefinitionProvider<K extends FieldKey<K>> implements ColumnDefinitionProvider<FieldMapperColumnDefinition<K>, K> {
    @Override
    public FieldMapperColumnDefinition<K> getColumnDefinition(K key) {
        return FieldMapperColumnDefinition.<K>identity();
    }

    @Override
    public <CP extends ColumnProperty, BC extends BiConsumer<Predicate<? super K>, CP>> BC forEach(Class<CP> propertyType, BC consumer) {
        return consumer;
    }
}
