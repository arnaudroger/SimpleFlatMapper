package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;

public class BoxedBooleanCellValueReader implements BooleanCellValueReader {
    private final CellValueReader<Boolean> reader;

    public BoxedBooleanCellValueReader(CellValueReader<Boolean> customReader) {
        this.reader = customReader;
    }

    @Override
    public boolean readBoolean(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return reader.read(chars, offset, length, parsingContext);
    }

    @Override
    public Boolean read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return reader.read(chars, offset, length, parsingContext);
    }

    @Override
    public String toString() {
        return "BoxedBooleanCellValueReader{" +
                "reader=" + reader +
                '}';
    }
}
