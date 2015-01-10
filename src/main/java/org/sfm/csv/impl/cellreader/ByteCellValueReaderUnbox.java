package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;


public class ByteCellValueReaderUnbox implements ByteCellValueReader {
    private final CellValueReader<Byte> reader;

    public ByteCellValueReaderUnbox(CellValueReader<Byte> customReader) {
        this.reader = customReader;
    }

    @Override
    public byte readByte(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return read(chars, offset, length, parsingContext).byteValue();
    }

    @Override
    public Byte read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return reader.read(chars, offset, length, parsingContext);
    }
}
