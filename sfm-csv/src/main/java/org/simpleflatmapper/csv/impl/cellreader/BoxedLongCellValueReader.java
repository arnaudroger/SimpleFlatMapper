package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;


public class BoxedLongCellValueReader implements LongCellValueReader {
    private final CellValueReader<Long> reader;

    public BoxedLongCellValueReader(CellValueReader<Long> customReader) {
        this.reader = customReader;
    }

    @Override
    public long readLong(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return read(chars, offset, length, parsingContext);
    }

    @Override
    public Long read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return reader.read(chars, offset, length, parsingContext);
    }

    @Override
    public String toString() {
        return "BoxedLongCellValueReader{" +
                "reader=" + reader +
                '}';
    }
}
