package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;


public final class StringCellValueReader implements CellValueReader<String> {


	@Override
	public String read(CharSequence value, ParsingContext parsingContext) {
		return readString(value);
	}

	public static String readString(CharSequence value) {
		return String.valueOf(value);
	}

    @Override
    public String toString() {
        return "StringCellValueReader{}";
    }
}
