package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.DecoderContext;

public class ByteCellValueReader implements CellValueReader<Byte> {

	@Override
	public Byte read(byte[] bytes, int offset, int length, DecoderContext decoderContext) {
		return new Byte((byte)IntegerCellValueReader.parseInt(bytes, offset, length));
	}
	
	@Override
	public Byte read(char[] chars, int offset, int length) {
		return new Byte((byte)IntegerCellValueReader.parseInt(chars, offset, length));
	}
}
