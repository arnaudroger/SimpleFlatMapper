package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;

public class DoubleCellValueReader implements CellValueReader<Double> {

	@Override
	public Double read(byte[] bytes, int offset, int length) {
		return new Double(parseDouble(bytes, offset, length));
	}

	public static double parseDouble(byte[] bytes, int offset, int length) {
		return Double.parseDouble(StringCellValueReader.readString(bytes, offset, length));
	}

}
