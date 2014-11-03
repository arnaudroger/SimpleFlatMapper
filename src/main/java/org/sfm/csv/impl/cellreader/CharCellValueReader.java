package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;

public class CharCellValueReader implements CellValueReader<Character> {

	@Override
	public Character read(char[] bytes, int offset, int length, ParsingContext parsingContext) {
		return new Character((char)IntegerCellValueReader.parseInt(bytes, offset, length));
	}
}
