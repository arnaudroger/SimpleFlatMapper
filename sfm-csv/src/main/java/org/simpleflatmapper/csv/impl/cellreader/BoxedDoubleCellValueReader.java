package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;


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
