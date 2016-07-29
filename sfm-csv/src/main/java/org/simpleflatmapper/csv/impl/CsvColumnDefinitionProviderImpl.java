package org.simpleflatmapper.csv.impl;


import org.simpleflatmapper.csv.CsvColumnDefinition;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.sfm.map.column.ColumnProperty;
import org.sfm.map.mapper.AbstractColumnDefinitionProvider;
import org.sfm.tuples.Tuple2;
import org.sfm.utils.BiConsumer;
import org.sfm.utils.Predicate;
import org.sfm.utils.UnaryFactory;

import java.util.List;

public class CsvColumnDefinitionProviderImpl extends AbstractColumnDefinitionProvider<CsvColumnDefinition, CsvColumnKey> {
    public CsvColumnDefinitionProviderImpl(){
    }

    public CsvColumnDefinitionProviderImpl(List<Tuple2<Predicate<? super CsvColumnKey>, CsvColumnDefinition>> definitions,
                                           List<Tuple2<Predicate<? super CsvColumnKey>, UnaryFactory<? super CsvColumnKey, ColumnProperty>>> properties) {
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
