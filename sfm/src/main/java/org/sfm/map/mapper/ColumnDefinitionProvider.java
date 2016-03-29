package org.sfm.map.mapper;


import org.sfm.map.FieldKey;
import org.sfm.map.column.ColumnProperty;
import org.sfm.utils.BiConsumer;
import org.sfm.utils.Predicate;

public interface ColumnDefinitionProvider<C extends ColumnDefinition<K, C>, K extends FieldKey<K>> {
    C getColumnDefinition(K key);
    <CP extends ColumnProperty, BC extends BiConsumer<Predicate<? super K>, CP>> BC forEach(Class<CP> propertyType, BC consumer);
}
