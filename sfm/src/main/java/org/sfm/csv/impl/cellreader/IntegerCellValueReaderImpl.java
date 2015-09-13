package org.sfm.csv.impl.cellreader;

import org.sfm.csv.ParsingContext;
import org.sfm.csv.impl.ParsingException;


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
		int n = 0;
		boolean negative = false;
		for(int i = offset; i < offset + length; i++) {
			char b = chars[i];
			if (b >= C_ZERO && b <= C_NINE) {
				n  = n * 10 +  chars[i] - C_ZERO;
			} else {
				if (b == C_NEG_SIGN && i == offset) {
					negative = true;
				} else if (b != ' '){
					throw new ParsingException("Cannot parse " + new String(chars, offset, length) + " as an int");
				}
			}
		}
		if (negative) {
			n = 0 - n;
		}
		return n;
	}

    @Override
    public String toString() {
        return "IntegerCellValueReaderImpl{}";
    }
}
