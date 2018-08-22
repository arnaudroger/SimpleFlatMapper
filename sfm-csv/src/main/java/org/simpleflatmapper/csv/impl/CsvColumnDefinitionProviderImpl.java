package org.simpleflatmapper.csv.impl;


import org.simpleflatmapper.csv.CsvColumnDefinition;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.map.mapper.AbstractColumnDefinitionProvider;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;

import java.util.ArrayList;
import java.util.List;

public class CsvColumnDefinitionProviderImpl extends AbstractColumnDefinitionProvider<CsvColumnKey> {
    public CsvColumnDefinitionProviderImpl(){
    }

    public CsvColumnDefinitionProviderImpl(List<PredicatedColunnPropertyFactory<CsvColumnKey>> properties) {
        super(properties);
    }


    @Override
    public AbstractColumnDefinitionProvider<CsvColumnKey> copy() {
        return new CsvColumnDefinitionProviderImpl(new ArrayList<PredicatedColunnPropertyFactory<CsvColumnKey>>(properties));
    }

    @Override
    protected CsvColumnDefinition identity() {
        return CsvColumnDefinition.identity();
    }
}
