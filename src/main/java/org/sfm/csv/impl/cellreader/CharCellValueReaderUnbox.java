package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;


public class CharCellValueReaderUnbox implements CharCellValueReader {
    private final CellValueReader<Character> reader;

    public CharCellValueReaderUnbox(CellValueReader<Character> customReader) {
        this.reader = customReader;
    }


    @Override
    public char readChar(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return read(chars, offset, length, parsingContext);
    }

    @Override
    public Character read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return reader.read(chars, offset, length, parsingContext);
    }

    @Override
    public String toString() {
        return "CharCellValueReaderUnbox{" +
                "reader=" + reader +
                '}';
    }
}
