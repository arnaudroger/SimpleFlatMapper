package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.ParsingContext;

public final class IntegerCellValueReaderImpl implements IntegerCellValueReader {

	@Override
	public Integer read(char[] chars, int offset, int length, ParsingContext parsingContext) {
		if (length == 0) return null;
		return readInt(chars, offset, length, parsingContext);
	}

	@Override
	public int readInt(char[] chars, int offset, int length, ParsingContext parsingContext) {
		return parseInt(chars, offset, length);
	}

	public static int parseInt(char[] chars, int offset, int length) {
		if (length == 0) return 0;
		return Integer.parseInt(String.valueOf(chars, offset, length));
	}

    @Override
    public String toString() {
        return "IntegerCellValueReaderImpl{}";
    }
}
