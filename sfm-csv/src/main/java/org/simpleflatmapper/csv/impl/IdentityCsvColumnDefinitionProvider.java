package org.simpleflatmapper.csv.impl;

import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.map.mapper.ColumnDefinitionProvider;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.util.BiConsumer;
import org.simpleflatmapper.util.Predicate;

public class IdentityCsvColumnDefinitionProvider implements ColumnDefinitionProvider<CsvColumnKey> {
    @Override
    public FieldMapperColumnDefinition<CsvColumnKey> getColumnDefinition(CsvColumnKey key) {
        return FieldMapperColumnDefinition.<CsvColumnKey>identity();
}

    @Override
    public <CP, BC extends BiConsumer<Predicate<? super CsvColumnKey>, CP>> BC forEach(Class<CP> propertyType, BC consumer) {
        return consumer;
    }
}
