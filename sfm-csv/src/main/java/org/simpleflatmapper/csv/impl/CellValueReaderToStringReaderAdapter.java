package org.simpleflatmapper.csv.impl;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.lightningcsv.StringReader;

public class CellValueReaderToStringReaderAdapter<T> implements CellValueReader<T> {
    private final StringReader<T> delegate;

    public CellValueReaderToStringReaderAdapter(StringReader<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return delegate.read(new String(chars, offset, length));
    }
}
