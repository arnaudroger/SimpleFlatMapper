package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;


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
