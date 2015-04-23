package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;


public class ShortCellValueReaderUnbox implements ShortCellValueReader {
    private final CellValueReader<Short> reader;

    public ShortCellValueReaderUnbox(CellValueReader<Short> customReader) {
        this.reader = customReader;
    }


    @Override
    public short readShort(CharSequence value, ParsingContext parsingContext) {
        return read(value, parsingContext);
    }

    @Override
    public Short read(CharSequence value, ParsingContext parsingContext) {
        return reader.read(value, parsingContext);
    }

    @Override
    public String toString() {
        return "ShortCellValueReaderUnbox{" +
                "reader=" + reader +
                '}';
    }
}
