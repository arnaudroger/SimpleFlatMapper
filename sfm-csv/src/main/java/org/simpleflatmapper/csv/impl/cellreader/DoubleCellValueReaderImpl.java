package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.ParsingContext;

public final class DoubleCellValueReaderImpl implements DoubleCellValueReader {

	@Override
	public Double read(char[] chars, int offset, int length, ParsingContext parsingContext) {
		if (length == 0) return null;
		return readDouble(chars, offset, length, parsingContext);
	}

	@Override
	public double readDouble(char[] chars, int offset, int length, ParsingContext parsingContext) {
		return parseDouble(chars, offset, length);
	}
	public static double parseDouble(char[] chars, int offset, int length) {
        if (length == 0) return Double.NaN;
        return Double.parseDouble(new String(chars, offset, length));
	}

    @Override
    public String toString() {
        return "DoubleCellValueReaderImpl{}";
    }
}
