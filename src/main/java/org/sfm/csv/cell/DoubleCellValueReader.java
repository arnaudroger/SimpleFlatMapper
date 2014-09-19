package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;

public class DoubleCellValueReader implements CellValueReader<Double> {

	private final StringCellValueReader stringReader = new StringCellValueReader();
	@Override
	public Double read(byte[] bytes, int offset, int length) {
		return new Double(stringReader.read(bytes, offset, length));
	}

}
