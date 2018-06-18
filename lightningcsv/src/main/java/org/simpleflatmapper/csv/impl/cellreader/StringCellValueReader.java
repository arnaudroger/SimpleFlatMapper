package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;


public final class StringCellValueReader implements CellValueReader<String> {

	@Override
	public String read(char[] chars, int offset, int length, ParsingContext parsingContext) {
		return new String(chars, offset, length);
	}

    @Override
    public String toString() {
        return "StringCellValueReader{}";
    }
}
