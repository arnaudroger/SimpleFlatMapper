package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;

public class DoubleCellValueReader implements CellValueReader<Double> {
	private final StringCellValueReader reader = new StringCellValueReader();

	@Override
	public Double read(byte[] bytes, int offset, int length) {
		return new Double(parseDouble(bytes, offset, length));
	}
	
	@Override
	public Double read(char[] chars, int offset, int length) {
		return new Double(parseDouble(chars, offset, length));
	}

	public double parseDouble(byte[] bytes, int offset, int length) {
		return Double.parseDouble(reader.read(bytes, offset, length));
	}
	
	public static double parseDouble(char[] chars, int offset, int length) {
		return Double.parseDouble(StringCellValueReader.readString(chars, offset, length));
	}
}
