package org.simpleflatmapper.csv.impl;


import org.simpleflatmapper.csv.CsvColumnDefinition;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.map.mapper.AbstractColumnDefinitionProvider;

import java.util.ArrayList;
import java.util.List;

public class CsvColumnDefinitionProviderImpl extends AbstractColumnDefinitionProvider<CsvColumnDefinition, CsvColumnKey> {
    public CsvColumnDefinitionProviderImpl(){
    }

    public CsvColumnDefinitionProviderImpl(List<PredicatedColunnPropertyFactory<CsvColumnDefinition, CsvColumnKey>> properties) {
        super(properties);
    }


    public CsvColumnDefinitionProviderImpl copy() {
        return new CsvColumnDefinitionProviderImpl(new ArrayList<PredicatedColunnPropertyFactory<CsvColumnDefinition, CsvColumnKey>>(properties));
    }

    @Override
    protected CsvColumnDefinition identity() {
        return CsvColumnDefinition.identity();
    }
}
