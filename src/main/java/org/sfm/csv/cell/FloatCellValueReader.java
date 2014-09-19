package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;

public class FloatCellValueReader implements CellValueReader<Float> {

	private final StringCellValueReader stringReader = new StringCellValueReader();
	@Override
	public Float read(byte[] bytes, int offset, int length) {
		return new Float(stringReader.read(bytes, offset, length));
	}

}
