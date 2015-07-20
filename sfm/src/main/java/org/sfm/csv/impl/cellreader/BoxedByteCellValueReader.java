package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.ParsingContext;


public class BoxedByteCellValueReader implements ByteCellValueReader {
    private final CellValueReader<Byte> reader;

    public BoxedByteCellValueReader(CellValueReader<Byte> customReader) {
        this.reader = customReader;
    }

    @Override
    public byte readByte(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return read(chars, offset, length, parsingContext);
    }

    @Override
    public Byte read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return reader.read(chars, offset, length, parsingContext);
    }

    @Override
    public String toString() {
        return "BoxedByteCellValueReader{" +
                "reader=" + reader +
                '}';
    }
}
