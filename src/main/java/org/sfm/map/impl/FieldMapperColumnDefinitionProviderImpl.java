package org.sfm.map.impl;


import org.sfm.map.FieldKey;

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
