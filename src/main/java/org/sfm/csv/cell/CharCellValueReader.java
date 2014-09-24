package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;

public class CharCellValueReader implements CellValueReader<Character> {

	@Override
	public Character read(byte[] bytes, int offset, int length) {
		return new Character((char)IntegerCellValueReader.parseInt(bytes, offset, length));
	}
	
	@Override
	public Character read(char[] bytes, int offset, int length) {
		return new Character((char)IntegerCellValueReader.parseInt(bytes, offset, length));
	}
}
