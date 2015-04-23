package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;


public class LongCellValueReaderUnbox implements LongCellValueReader {
    private final CellValueReader<Long> reader;

    public LongCellValueReaderUnbox(CellValueReader<Long> customReader) {
        this.reader = customReader;
    }

    @Override
    public long readLong(CharSequence value, ParsingContext parsingContext) {
        return read(value, parsingContext);
    }

    @Override
    public Long read(CharSequence value, ParsingContext parsingContext) {
        return reader.read(value, parsingContext);
    }

    @Override
    public String toString() {
        return "LongCellValueReaderUnbox{" +
                "reader=" + reader +
                '}';
    }
}
