package org.sfm.csv.impl;

import org.sfm.csv.CsvColumnDefinition;
import org.sfm.csv.CsvColumnKey;
import org.sfm.map.mapper.ColumnDefinitionProvider;

public class IdentityCsvColumnDefinitionProvider implements ColumnDefinitionProvider<CsvColumnDefinition, CsvColumnKey> {
    @Override
    public CsvColumnDefinition getColumnDefinition(CsvColumnKey key) {
        return CsvColumnDefinition.identity();
}
}
