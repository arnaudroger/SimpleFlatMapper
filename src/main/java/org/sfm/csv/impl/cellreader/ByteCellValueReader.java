package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;

public class ByteCellValueReader implements CellValueReader<Byte> {

	@Override
	public Byte read(char[] chars, int offset, int length, ParsingContext parsingContext) {
		return new Byte((byte)IntegerCellValueReader.parseInt(chars, offset, length));
	}
}
