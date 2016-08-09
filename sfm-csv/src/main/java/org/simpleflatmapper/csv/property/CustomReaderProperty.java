package org.simpleflatmapper.csv.property;


import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public class CustomReaderProperty {
    private final CellValueReader<?> reader;

    public CustomReaderProperty(CellValueReader<?> reader) {
        this.reader = requireNonNull("reader",reader);
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
