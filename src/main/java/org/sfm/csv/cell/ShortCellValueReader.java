package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;

public class ShortCellValueReader implements CellValueReader<Short> {

	@Override
	public Short read(char[] chars, int offset, int length) {
		return new Short((short)IntegerCellValueReader.parseInt(chars, offset, length));
	}
}
