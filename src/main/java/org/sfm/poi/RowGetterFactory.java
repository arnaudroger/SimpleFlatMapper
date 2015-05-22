package org.sfm.poi;


import org.apache.poi.ss.usermodel.Row;
import org.sfm.csv.CsvColumnKey;
import org.sfm.map.ColumnDefinition;
import org.sfm.map.GetterFactory;
import org.sfm.reflect.Getter;

import java.lang.reflect.Type;

public class RowGetterFactory implements GetterFactory<Row, CsvColumnKey> {

    @Override
    public <P> Getter<Row, P> newGetter(Type target, CsvColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
        return null;
    }
}
