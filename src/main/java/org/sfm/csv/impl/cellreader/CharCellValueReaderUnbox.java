package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;


public class CharCellValueReaderUnbox implements CharCellValueReader {
    private final CellValueReader<Character> reader;

    public CharCellValueReaderUnbox(CellValueReader<Character> customReader) {
        this.reader = customReader;
    }


    @Override
    public char readChar(CharSequence value, ParsingContext parsingContext) {
        return read(value, parsingContext);
    }

    @Override
    public Character read(CharSequence value, ParsingContext parsingContext) {
        return reader.read(value, parsingContext);
    }

    @Override
    public String toString() {
        return "CharCellValueReaderUnbox{" +
                "reader=" + reader +
                '}';
    }
}
