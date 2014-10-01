package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.ParsingContext;

public class ShortCellValueReader implements CellValueReader<Short> {

	@Override
	public Short read(char[] chars, int offset, int length, ParsingContext parsingContext) {
		return new Short((short)IntegerCellValueReader.parseInt(chars, offset, length));
	}
}
