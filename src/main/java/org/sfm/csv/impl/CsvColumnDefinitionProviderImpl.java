package org.sfm.csv.impl;


import org.sfm.csv.CsvColumnDefinition;
import org.sfm.csv.CsvColumnKey;
import org.sfm.map.impl.AbstractColumnDefinitionProvider;

public class CsvColumnDefinitionProviderImpl extends AbstractColumnDefinitionProvider<CsvColumnDefinition, CsvColumnKey> {
    @Override
    protected CsvColumnDefinition compose(CsvColumnDefinition definition, CsvColumnDefinition second) {
        return CsvColumnDefinition.compose(definition, second);
    }

    @Override
    protected CsvColumnDefinition identity() {
        return CsvColumnDefinition.IDENTITY;
    }
}
