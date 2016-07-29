package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;


public class BoxedFloatCellValueReader implements FloatCellValueReader {
    private final CellValueReader<Float> reader;

    public BoxedFloatCellValueReader(CellValueReader<Float> customReader) {
        this.reader = customReader;
    }

    @Override
    public float readFloat(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return read(chars, offset, length, parsingContext);
    }

    @Override
    public Float read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return reader.read(chars, offset, length, parsingContext);
    }

    @Override
    public String toString() {
        return "BoxedFloatCellValueReader{" +
                "reader=" + reader +
                '}';
    }
}
