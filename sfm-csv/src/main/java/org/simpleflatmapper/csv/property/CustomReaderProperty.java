package org.simpleflatmapper.csv.property;


import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.impl.CellValueReaderToStringReaderAdapter;
import org.simpleflatmapper.lightningcsv.StringReader;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;

import static org.simpleflatmapper.util.Asserts.requireNonNull;

public class CustomReaderProperty {
    private final CellValueReader<?> reader;
    
    public <T> CustomReaderProperty(StringReader<T> reader) {
        this.reader = new CellValueReaderToStringReaderAdapter<T>(requireNonNull("reader",reader));
    }
    public CustomReaderProperty(CellValueReader<?> reader) {
        this.reader = requireNonNull("reader",reader);
    }

    public CellValueReader<?> getReader() {
        return reader;
    }

    public Type getReturnType() {
        final Type[] paramTypesForInterface = TypeHelper.getGenericParameterForClass(reader.getClass(), CellValueReader.class);
        return paramTypesForInterface != null ? paramTypesForInterface[0] : null;
    }


    @Override
    public String toString() {
        return "CustomReader{CellValueReader}";
    }
}
