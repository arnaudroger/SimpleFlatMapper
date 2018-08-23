package org.simpleflatmapper.csv.impl;

import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.map.context.KeySourceGetter;

public class CsvRowKeySourceGetter implements KeySourceGetter<CsvColumnKey, CsvRow> {
    
    public static final CsvRowKeySourceGetter INSTANCE = new CsvRowKeySourceGetter();
    
    private CsvRowKeySourceGetter() {
        
    }
    @Override
    public Object getValue(CsvColumnKey key, CsvRow source) {
        return source.getString(key.getIndex());
    }
}
