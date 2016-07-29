package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.ParsingContext;

public final class CharCellValueReaderImpl implements CharCellValueReader {

	@Override
	public Character read(char[] bytes, int offset, int length, ParsingContext parsingContext) {
		if (length == 0) return null;
		return readChar(bytes, offset, length, parsingContext);
	}

	@Override
	public char readChar(char[] bytes, int offset, int length, ParsingContext parsingContext) {
		return (char) IntegerCellValueReaderImpl.parseInt(bytes, offset, length);
	}

    @Override
    public String toString() {
        return "CharCellValueReaderImpl{}";
    }
}
