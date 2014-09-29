package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.DecoderContext;

public class DoubleCellValueReader implements CellValueReader<Double> {
	private final StringCellValueReader reader = new StringCellValueReader();

	@Override
	public Double read(byte[] bytes, int offset, int length, DecoderContext decoderContext) {
		return new Double(parseDouble(bytes, offset, length, decoderContext));
	}
	
	@Override
	public Double read(char[] chars, int offset, int length) {
		return new Double(parseDouble(chars, offset, length));
	}

	public double parseDouble(byte[] bytes, int offset, int length, DecoderContext decoderContext) {
		return Double.parseDouble(reader.read(bytes, offset, length, decoderContext));
	}
	
	public static double parseDouble(char[] chars, int offset, int length) {
		return Double.parseDouble(StringCellValueReader.readString(chars, offset, length));
	}
}
