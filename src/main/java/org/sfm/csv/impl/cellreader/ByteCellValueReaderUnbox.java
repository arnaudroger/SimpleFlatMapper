package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;


public class ByteCellValueReaderUnbox implements ByteCellValueReader {
    private final CellValueReader<Byte> reader;

    public ByteCellValueReaderUnbox(CellValueReader<Byte> customReader) {
        this.reader = customReader;
    }

    @Override
    public byte readByte(CharSequence value, ParsingContext parsingContext) {
        return read(value, parsingContext);
    }

    @Override
    public Byte read(CharSequence value, ParsingContext parsingContext) {
        return reader.read(value, parsingContext);
    }

    @Override
    public String toString() {
        return "ByteCellValueReaderUnbox{" +
                "reader=" + reader +
                '}';
    }
}
