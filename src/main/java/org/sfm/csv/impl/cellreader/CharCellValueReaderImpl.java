package org.sfm.csv.impl.cellreader;

import org.sfm.csv.impl.ParsingContext;

public final class CharCellValueReaderImpl implements CharCellValueReader {

	@Override
	public Character read(CharSequence value, ParsingContext parsingContext) {
		if (value.length() == 0) return null;
		return readChar(value, parsingContext);
	}

	@Override
	public char readChar(CharSequence value, ParsingContext parsingContext) {
		return (char) IntegerCellValueReaderImpl.parseInt(value);
	}

    @Override
    public String toString() {
        return "CharCellValueReaderImpl{}";
    }
}
