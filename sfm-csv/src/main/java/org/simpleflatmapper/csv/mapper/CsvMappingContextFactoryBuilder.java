package org.simpleflatmapper.csv.mapper;


import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.map.context.KeySourceGetter;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;


public class CsvMappingContextFactoryBuilder extends MappingContextFactoryBuilder<CsvRow, CsvColumnKey> {
    public CsvMappingContextFactoryBuilder() {
        super(new CsvRowKeySourceGetter());
    }

    private static class CsvRowKeySourceGetter implements KeySourceGetter<CsvColumnKey, CsvRow> {
        @Override
        public Object getValue(CsvColumnKey key, CsvRow source) {
            return source.getString(key.getIndex());
        }
    }
}
