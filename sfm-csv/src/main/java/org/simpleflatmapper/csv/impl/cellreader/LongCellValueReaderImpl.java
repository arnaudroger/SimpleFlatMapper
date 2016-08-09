package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.impl.ParsingException;


public final class LongCellValueReaderImpl implements LongCellValueReader {

    @Override
    public Long read(char[] chars, int offset, int length, ParsingContext parsingContext) {
        if (length == 0) return null;
        return readLong(chars, offset, length, parsingContext);
    }

    @Override
    public long readLong(char[] chars, int offset, int length, ParsingContext parsingContext) {
        return parseLong(chars, offset, length);
    }

    public static long parseLong(char[] chars, int offset, int length) {
        if (length == 0) return 0;
        return Long.parseLong(String.valueOf(chars, offset, length));
    }

    @Override
    public String toString() {
        return "LongCellValueReaderImpl{}";
    }
}
