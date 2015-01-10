package org.sfm.csv.impl.cellreader;

import org.sfm.csv.impl.ParsingContext;

public final class CharCellValueReaderImpl implements CharCellValueReader {

	@Override
	public Character read(char[] bytes, int offset, int length, ParsingContext parsingContext) {
		return new Character(readChar(bytes, offset, length, parsingContext));
	}

	@Override
	public char readChar(char[] bytes, int offset, int length, ParsingContext parsingContext) {
		return (char) IntegerCellValueReaderImpl.parseInt(bytes, offset, length);
	}
}
