package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;


public final class StringCellValueReader implements CellValueReader<String> {

	@Override
	public String read(char[] chars, int offset, int length, ParsingContext parsingContext) {
		return readString(chars, offset, length);
	}

	public static String readString(char[] chars, int offset, int length) {
		return String.valueOf(chars, offset, length);
	}

    @Override
    public String toString() {
        return "StringCellValueReader{}";
    }
}
