package org.sfm.csv.impl.cellreader;

import org.sfm.csv.impl.ParsingContext;

public final class DoubleCellValueReaderImpl implements DoubleCellValueReader {

	@Override
	public Double read(char[] chars, int offset, int length, ParsingContext parsingContext) {
		if (length == 0) return null;
		return new Double(readDouble(chars, offset, length, parsingContext));
	}

	@Override
	public double readDouble(char[] chars, int offset, int length, ParsingContext parsingContext) {
		return parseDouble(chars, offset, length);
	}
	public static double parseDouble(char[] chars, int offset, int length) {
		return Double.parseDouble(StringCellValueReader.readString(chars, offset, length));
	}
}
