package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.ParsingContext;

public final class BooleanCellValueReaderImpl implements BooleanCellValueReader {

	@Override
	public Boolean read(char[] chars, int offset, int length, ParsingContext parsingContext) {
		if (length == 0) return null;
		return readBoolean(chars, offset, length, parsingContext);
	}

	@Override
	public boolean readBoolean(char[] chars, int offset, int length, ParsingContext parsingContext) {
		return parseBoolean(chars, offset, length);
	}

	public static boolean parseBoolean(char[] chars, int offset, int length) {
		switch (length) {
		case 0:
			return false;
		case 1:
			switch (chars[offset]) {
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
			if ((chars[offset] == 'N' || chars[offset] == 'n')
				&& (chars[offset + 1] == 'O' || chars[offset + 1] == 'o')) {
				return false;
			}
		case 5:
			if (
				(chars[offset] == 'F' || chars[offset] == 'f')
				&& (chars[offset + 1] == 'A' || chars[offset + 1] == 'a')
				&& (chars[offset + 2] == 'L' || chars[offset + 2] == 'l')
				&& (chars[offset + 3] == 'S' || chars[offset + 3] == 's')
				&& (chars[offset + 4] == 'E' || chars[offset + 4] == 'e')
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
