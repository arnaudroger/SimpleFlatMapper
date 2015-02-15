package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;


public class LongCellValueReaderUnbox implements LongCellValueReader {
    private final CellValueReader<Long> reader;

    public LongCellValueReaderUnbox(CellValueReader<Long> customReader) {
        this.reader = customReader;
    }

    @Override
    public long readLong(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return read(chars, offset, length, parsingContext).longValue();
    }

    @Override
    public Long read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return reader.read(chars, offset, length, parsingContext);
    }

    @Override
    public String toString() {
        return "LongCellValueReaderUnbox{" +
                "reader=" + reader +
                '}';
    }
}
