package org.simpleflatmapper.map.mapper;


import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;

import java.util.List;

public class FieldMapperColumnDefinitionProviderImpl<K extends FieldKey<K>> extends AbstractColumnDefinitionProvider<K> {
    public FieldMapperColumnDefinitionProviderImpl() {
    }

    public FieldMapperColumnDefinitionProviderImpl(List<PredicatedColumnPropertyFactory<K>> properties) {
        super(properties);
    }

    @Override
    public AbstractColumnDefinitionProvider<K> copy() {
        return new FieldMapperColumnDefinitionProviderImpl<K>(properties);
    }

    @Override
    protected FieldMapperColumnDefinition<K> identity() {
        return FieldMapperColumnDefinition.<K>identity();
        
    }
}
