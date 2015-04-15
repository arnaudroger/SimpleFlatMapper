package org.sfm.csv.column;


import org.sfm.csv.CellValueReader;
import org.sfm.map.column.ColumnProperty;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;

public class CustomReaderProperty implements ColumnProperty {
    private final CellValueReader<?> reader;

    public CustomReaderProperty(CellValueReader<?> reader) {
        if (reader == null) throw new NullPointerException();
        this.reader = reader;
    }

    public CellValueReader<?> getReader() {
        return reader;
    }

    public Type getReturnType() {
        final Type[] paramTypesForInterface = TypeHelper.getParamTypesForInterface(reader.getClass(), CellValueReader.class);
        return paramTypesForInterface != null ? paramTypesForInterface[0] : null;
    }


    @Override
    public String toString() {
        return "CustomReader{CellValueReader}";
    }
}
