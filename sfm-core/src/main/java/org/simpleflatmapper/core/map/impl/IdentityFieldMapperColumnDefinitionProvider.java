package org.simpleflatmapper.core.map.impl;


import org.simpleflatmapper.core.map.column.ColumnProperty;
import org.simpleflatmapper.core.map.mapper.ColumnDefinitionProvider;
import org.simpleflatmapper.core.map.FieldKey;
import org.simpleflatmapper.core.map.column.FieldMapperColumnDefinition;
import org.simpleflatmapper.core.utils.BiConsumer;
import org.simpleflatmapper.core.utils.Predicate;

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
