package org.simpleflatmapper.csv.impl;


import org.simpleflatmapper.csv.CsvColumnDefinition;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.core.map.mapper.AbstractColumnDefinitionProvider;

import java.util.List;

public class CsvColumnDefinitionProviderImpl extends AbstractColumnDefinitionProvider<CsvColumnDefinition, CsvColumnKey> {
    public CsvColumnDefinitionProviderImpl(){
    }

    public CsvColumnDefinitionProviderImpl(List<PredicatedColunnDefinition<CsvColumnDefinition, CsvColumnKey>> definitions,
                                           List<PredicatedColunnPropertyFactory<CsvColumnDefinition, CsvColumnKey>> properties) {
        super(definitions, properties);
    }

    @Override
    protected CsvColumnDefinition compose(CsvColumnDefinition definition, CsvColumnDefinition second) {
        return CsvColumnDefinition.compose(definition, second);
    }

    @Override
    protected CsvColumnDefinition identity() {
        return CsvColumnDefinition.identity();
    }
}
