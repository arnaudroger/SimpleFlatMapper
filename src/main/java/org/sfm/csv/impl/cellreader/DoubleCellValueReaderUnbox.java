package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;


public class DoubleCellValueReaderUnbox implements DoubleCellValueReader {
    private final CellValueReader<Double> reader;

    public DoubleCellValueReaderUnbox(CellValueReader<Double> customReader) {
        this.reader = customReader;
    }

    @Override
    public double readDouble(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return read(chars, offset, length, parsingContext).doubleValue();
    }

    @Override
    public Double read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return reader.read(chars, offset, length, parsingContext);
    }
}
