package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;


public class BoxedIntegerCellValueReader implements IntegerCellValueReader {
    private final CellValueReader<Integer> reader;

    public BoxedIntegerCellValueReader(CellValueReader<Integer> customReader) {
        this.reader = customReader;
    }

    @Override
    public int readInt(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return read(chars, offset, length, parsingContext);
    }

    @Override
    public Integer read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return reader.read(chars, offset, length, parsingContext);
    }

    @Override
    public String toString() {
        return "BoxedIntegerCellValueReader{" +
                "reader=" + reader +
                '}';
    }
}
