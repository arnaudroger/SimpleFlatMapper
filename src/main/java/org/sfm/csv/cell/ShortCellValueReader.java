package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;

public class ShortCellValueReader implements CellValueReader<Short> {

	@Override
	public Short read(byte[] bytes, int offset, int length) {
		return new Short((short)IntegerCellValueReader.parseInt(bytes, offset, length));
	}

}
