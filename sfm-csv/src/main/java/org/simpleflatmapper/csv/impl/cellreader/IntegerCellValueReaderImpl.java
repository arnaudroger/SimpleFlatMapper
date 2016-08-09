package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.impl.ParsingException;


public final class IntegerCellValueReaderImpl implements IntegerCellValueReader {

	private final static char C_ZERO = '0';
	private final static char C_NINE = '9';
	private final static char C_NEG_SIGN = '-';

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
		return Integer.parseInt(String.valueOf(chars, offset, length));
	}

    @Override
    public String toString() {
        return "IntegerCellValueReaderImpl{}";
    }
}
