package org.simpleflatmapper.core.map.mapper;


import org.simpleflatmapper.core.map.FieldKey;
import org.simpleflatmapper.core.map.column.ColumnProperty;
import org.simpleflatmapper.core.utils.BiConsumer;
import org.simpleflatmapper.core.utils.Predicate;

public interface ColumnDefinitionProvider<C extends ColumnDefinition<K, C>, K extends FieldKey<K>> {
    C getColumnDefinition(K key);
    <CP extends ColumnProperty, BC extends BiConsumer<Predicate<? super K>, CP>> BC forEach(Class<CP> propertyType, BC consumer);
}
