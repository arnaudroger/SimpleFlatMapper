package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;


public class IntegerCellValueReaderUnbox implements IntegerCellValueReader {
    private final CellValueReader<Integer> reader;

    public IntegerCellValueReaderUnbox(CellValueReader<Integer> customReader) {
        this.reader = customReader;
    }

    @Override
    public int readInt(CharSequence value, ParsingContext parsingContext) {
        return read(value, parsingContext);
    }

    @Override
    public Integer read(CharSequence value, ParsingContext parsingContext) {
        return reader.read(value, parsingContext);
    }

    @Override
    public String toString() {
        return "IntegerCellValueReaderUnbox{" +
                "reader=" + reader +
                '}';
    }
}
