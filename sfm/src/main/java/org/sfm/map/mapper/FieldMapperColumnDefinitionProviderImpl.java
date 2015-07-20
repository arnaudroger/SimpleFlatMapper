package org.sfm.map.mapper;


import org.sfm.map.mapper.AbstractColumnDefinitionProvider;
import org.sfm.map.FieldKey;
import org.sfm.map.column.FieldMapperColumnDefinition;

public class FieldMapperColumnDefinitionProviderImpl<K extends FieldKey<K>, S> extends AbstractColumnDefinitionProvider<FieldMapperColumnDefinition<K, S>, K> {
    @Override
    protected FieldMapperColumnDefinition<K, S> compose(FieldMapperColumnDefinition<K, S> definition, FieldMapperColumnDefinition<K, S> second) {
        return FieldMapperColumnDefinition.compose(definition, second);
    }

    @Override
    protected FieldMapperColumnDefinition<K, S> identity() {
        return FieldMapperColumnDefinition.<K,S>identity();
    }
}
