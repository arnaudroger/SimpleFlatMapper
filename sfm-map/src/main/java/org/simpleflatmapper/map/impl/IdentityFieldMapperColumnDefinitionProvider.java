package org.simpleflatmapper.map.impl;


import org.simpleflatmapper.map.mapper.ColumnDefinitionProvider;
import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.util.BiConsumer;
import org.simpleflatmapper.util.Predicate;

public class IdentityFieldMapperColumnDefinitionProvider<K extends FieldKey<K>> implements ColumnDefinitionProvider<K> {
    @Override
    public FieldMapperColumnDefinition<K> getColumnDefinition(K key) {
        return FieldMapperColumnDefinition.<K>identity();
    }

    @Override
    public <CP, BC extends BiConsumer<Predicate<? super K>, CP>> BC forEach(Class<CP> propertyType, BC consumer) {
        return consumer;
    }
}
