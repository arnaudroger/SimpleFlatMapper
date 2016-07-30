package org.sfm.csv.impl.cellreader.joda;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.ParsingContext;

public class MultiFormaterCellValueReader<T> implements CellValueReader<T> {

    private final CellValueReader<T>[] readers;

    public MultiFormaterCellValueReader(CellValueReader<T>[] readers) {
        this.readers = readers;
    }

    @Override
    public T read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        for(CellValueReader<T> reader : readers) {
            try {
                return reader.read(chars, offset, length, parsingContext);
            } catch(IllegalArgumentException e) {
            }
        }
        throw new IllegalArgumentException("Unable to parse date " + String.valueOf(chars, offset, length));
    }

}
