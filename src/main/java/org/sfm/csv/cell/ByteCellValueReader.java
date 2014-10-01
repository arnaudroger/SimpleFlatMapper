package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.ParsingContext;

public class ByteCellValueReader implements CellValueReader<Byte> {

	@Override
	public Byte read(char[] chars, int offset, int length, ParsingContext parsingContext) {
		return new Byte((byte)IntegerCellValueReader.parseInt(chars, offset, length));
	}
}
