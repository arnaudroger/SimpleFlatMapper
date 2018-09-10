package org.simpleflatmapper.map.mapper;


import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.util.BiConsumer;
import org.simpleflatmapper.util.Predicate;

public interface ColumnDefinitionProvider<K extends FieldKey<K>> {
    ColumnDefinition<K, ?> getColumnDefinition(K key);
    <CP, BC extends BiConsumer<Predicate<? super K>, CP>> BC forEach(Class<CP> propertyType, BC consumer);
}
