package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;


public class DoubleCellValueReaderUnbox implements DoubleCellValueReader {
    private final CellValueReader<Double> reader;

    public DoubleCellValueReaderUnbox(CellValueReader<Double> customReader) {
        this.reader = customReader;
    }

    @Override
    public double readDouble(CharSequence value, ParsingContext parsingContext) {
        return read(value, parsingContext);
    }

    @Override
    public Double read(CharSequence value, ParsingContext parsingContext) {
        return reader.read(value, parsingContext);
    }

    @Override
    public String toString() {
        return "DoubleCellValueReaderUnbox{" +
                "reader=" + reader +
                '}';
    }
}
