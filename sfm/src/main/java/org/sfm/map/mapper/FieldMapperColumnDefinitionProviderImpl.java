package org.sfm.map.mapper;


import org.sfm.map.FieldKey;
import org.sfm.map.column.FieldMapperColumnDefinition;

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
