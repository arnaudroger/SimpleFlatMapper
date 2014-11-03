package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;

public class ShortCellValueReader implements CellValueReader<Short> {

	@Override
	public Short read(char[] chars, int offset, int length, ParsingContext parsingContext) {
		return new Short((short)IntegerCellValueReader.parseInt(chars, offset, length));
	}
}
