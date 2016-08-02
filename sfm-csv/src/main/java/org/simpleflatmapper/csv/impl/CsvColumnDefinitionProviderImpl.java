package org.simpleflatmapper.csv.impl;


import org.simpleflatmapper.csv.CsvColumnDefinition;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.core.map.column.ColumnProperty;
import org.simpleflatmapper.core.map.mapper.AbstractColumnDefinitionProvider;
import org.simpleflatmapper.core.tuples.Tuple2;
import org.simpleflatmapper.core.utils.Predicate;
import org.simpleflatmapper.core.utils.UnaryFactory;

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
