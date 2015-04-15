package org.sfm.csv.impl;


import org.sfm.csv.CsvColumnDefinition;
import org.sfm.csv.CsvColumnKey;
import org.sfm.map.impl.AbstractColumnDefinitionProvider;
import org.sfm.tuples.Tuple2;
import org.sfm.utils.Predicate;

import java.util.List;

public class CsvColumnDefinitionProviderImpl extends AbstractColumnDefinitionProvider<CsvColumnDefinition, CsvColumnKey> {
    public CsvColumnDefinitionProviderImpl(){
    }

    public CsvColumnDefinitionProviderImpl(List<Tuple2<Predicate<? super CsvColumnKey>, CsvColumnDefinition>> definitions) {
        super(definitions);
    }

    @Override
    protected CsvColumnDefinition compose(CsvColumnDefinition definition, CsvColumnDefinition second) {
        return CsvColumnDefinition.compose(definition, second);
    }

    @Override
    protected CsvColumnDefinition identity() {
        return CsvColumnDefinition.identity();
    }

    public List<Tuple2<Predicate<? super CsvColumnKey>, CsvColumnDefinition>> getDefinitions() {
        return definitions;
    }
}
