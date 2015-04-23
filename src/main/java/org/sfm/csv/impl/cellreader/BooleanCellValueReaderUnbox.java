package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;

public class BooleanCellValueReaderUnbox implements BooleanCellValueReader {
    private final CellValueReader<Boolean> reader;

    public BooleanCellValueReaderUnbox(CellValueReader<Boolean> customReader) {
        this.reader = customReader;
    }

    @Override
    public boolean readBoolean(CharSequence value, ParsingContext parsingContext) {
        return reader.read(value, parsingContext);
    }

    @Override
    public Boolean read(CharSequence value, ParsingContext parsingContext) {
        return reader.read(value, parsingContext);
    }

    @Override
    public String toString() {
        return "BooleanCellValueReaderUnbox{" +
                "reader=" + reader +
                '}';
    }
}
