package org.simpleflatmapper.csv.impl;


import org.simpleflatmapper.csv.CsvColumnDefinition;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.map.mapper.AbstractColumnDefinitionProvider;

import java.util.ArrayList;
import java.util.List;

public class CsvColumnDefinitionProviderImpl extends AbstractColumnDefinitionProvider<CsvColumnKey> {
    public CsvColumnDefinitionProviderImpl(){
    }

    public CsvColumnDefinitionProviderImpl(List<PredicatedColumnPropertyFactory<CsvColumnKey>> properties) {
        super(properties);
    }


    @Override
    public AbstractColumnDefinitionProvider<CsvColumnKey> copy() {
        return new CsvColumnDefinitionProviderImpl(new ArrayList<PredicatedColumnPropertyFactory<CsvColumnKey>>(properties));
    }

    @Override
    protected CsvColumnDefinition identity() {
        return CsvColumnDefinition.identity();
    }
}
