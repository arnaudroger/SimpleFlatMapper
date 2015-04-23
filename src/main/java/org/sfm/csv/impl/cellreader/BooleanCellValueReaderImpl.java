package org.sfm.csv.impl.cellreader;

import org.sfm.csv.impl.ParsingContext;

public final class BooleanCellValueReaderImpl implements BooleanCellValueReader {

	@Override
	public Boolean read(CharSequence value, ParsingContext parsingContext) {
		if (value.length() == 0) return null;
		return readBoolean(value, parsingContext);
	}

	@Override
	public boolean readBoolean(CharSequence value, ParsingContext parsingContext) {
		return parseBoolean(value);
	}

	public static boolean parseBoolean(CharSequence value) {
		switch (value.length()) {
		case 0:
			return false;
		case 1:
			switch (value.charAt(0)) {
			case 0:
			case '0':
			case 'F':
			case 'f':
			case 'n':
			case 'N':
				return false;
			default:
				return true;
			}
		case 2:
			if ((value.charAt(0) == 'N' ||value.charAt(0) == 'n')
				&& (value.charAt(1) == 'O' ||value.charAt(1) == 'o')) {
				return false;
			}
		case 5:
			if (
				(value.charAt(0) == 'F' || value.charAt(0) == 'f')
				&& (value.charAt(1) == 'A' || value.charAt(1) == 'a')
				&& (value.charAt(2) == 'L' || value.charAt(2) == 'l')
				&& (value.charAt(3) == 'S' || value.charAt(3) == 's')
				&& (value.charAt(4) == 'E' || value.charAt(4) == 'e')
				) {
					return false;
				}
		}
		return true;
	}

    @Override
    public String toString() {
        return "BooleanCellValueReaderImpl{}";
    }
}
