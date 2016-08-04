package org.simpleflatmapper.core.map.mapper;


import org.simpleflatmapper.core.map.FieldKey;
import org.simpleflatmapper.core.map.column.FieldMapperColumnDefinition;

public class FieldMapperColumnDefinitionProviderImpl<K extends FieldKey<K>> extends AbstractColumnDefinitionProvider<FieldMapperColumnDefinition<K>, K> {
    @Override
    protected FieldMapperColumnDefinition<K> compose(FieldMapperColumnDefinition<K> definition, FieldMapperColumnDefinition<K> second) {
        return FieldMapperColumnDefinition.compose(definition, second);
    }

    @Override
    protected FieldMapperColumnDefinition<K> identity() {
        return FieldMapperColumnDefinition.<K>identity();
    }
}
