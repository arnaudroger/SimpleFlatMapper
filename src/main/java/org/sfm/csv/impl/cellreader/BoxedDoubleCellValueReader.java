package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;


public class BoxedDoubleCellValueReader implements DoubleCellValueReader {
    private final CellValueReader<Double> reader;

    public BoxedDoubleCellValueReader(CellValueReader<Double> customReader) {
        this.reader = customReader;
    }

    @Override
    public double readDouble(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return read(chars, offset, length, parsingContext);
    }

    @Override
    public Double read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return reader.read(chars, offset, length, parsingContext);
    }

    @Override
    public String toString() {
        return "BoxedDoubleCellValueReader{" +
                "reader=" + reader +
                '}';
    }
}
