package org.simpleflatmapper.csv.mapper;


import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.csv.impl.CsvRowKeySourceGetter;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;


public class CsvMappingContextFactoryBuilder extends MappingContextFactoryBuilder<CsvRow, CsvColumnKey> {
    public CsvMappingContextFactoryBuilder(boolean ignoreRootKey) {
        super(CsvRowKeySourceGetter.INSTANCE, ignoreRootKey);
    }

}
