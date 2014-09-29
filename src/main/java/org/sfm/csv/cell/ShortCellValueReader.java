package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.DecoderContext;

public class ShortCellValueReader implements CellValueReader<Short> {

	@Override
	public Short read(byte[] bytes, int offset, int length, DecoderContext decoderContext) {
		return new Short((short)IntegerCellValueReader.parseInt(bytes, offset, length));
	}

	@Override
	public Short read(char[] chars, int offset, int length) {
		return new Short((short)IntegerCellValueReader.parseInt(chars, offset, length));
	}
}
