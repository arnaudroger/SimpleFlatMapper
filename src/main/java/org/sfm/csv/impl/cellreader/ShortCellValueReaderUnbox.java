package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;


public class ShortCellValueReaderUnbox implements ShortCellValueReader {
    private final CellValueReader<Short> reader;

    public ShortCellValueReaderUnbox(CellValueReader<Short> customReader) {
        this.reader = customReader;
    }


    @Override
    public short readShort(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return read(chars, offset, length, parsingContext).shortValue();
    }

    @Override
    public Short read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return reader.read(chars, offset, length, parsingContext);
    }
}
