package org.sfm.csv.impl.cellreader;

import org.sfm.csv.impl.ParsingContext;
import org.sfm.csv.impl.ParsingException;


public final class IntegerCellValueReaderImpl implements IntegerCellValueReader {

	private final static char C_ZERO = '0';
	private final static char C_NINE = '9';
	private final static char C_NEG_SIGN = '-';

	@Override
	public Integer read(CharSequence value, ParsingContext parsingContext) {
		if (value.length() == 0) return null;
		return readInt(value, parsingContext);
	}

	@Override
	public int readInt(CharSequence value, ParsingContext parsingContext) {
		return parseInt(value);
	}

	public static int parseInt(CharSequence value) {
		int n = 0;
		boolean negative = false;
		for(int i = 0; i < value.length(); i++) {
			char b = value.charAt(i);
			if (b >= C_ZERO && b <= C_NINE) {
				n  = n * 10 +  b - C_ZERO;
			} else {
				if (b == C_NEG_SIGN && i == 0) {
					negative = true;
				} else {
					throw new ParsingException("Cannot parse " + value + " as an int");
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
