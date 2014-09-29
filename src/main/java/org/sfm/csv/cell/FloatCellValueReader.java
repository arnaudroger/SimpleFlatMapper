package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.DecoderContext;

public class FloatCellValueReader implements CellValueReader<Float> {
	private final StringCellValueReader reader = new StringCellValueReader();
	@Override
	public Float read(byte[] bytes, int offset, int length, DecoderContext decoderContext) {
		return new Float(parseFloat(bytes, offset, length, decoderContext));
	}

	@Override
	public Float read(char[] chars, int offset, int length) {
		return new Float(parseFloat(chars, offset, length));
	}
	
	public float parseFloat(byte[] bytes, int offset, int length, DecoderContext decoderContext) {
		return Float.parseFloat(reader.read(bytes, offset, length, decoderContext));
	}

	public static float parseFloat(char[] chars, int offset, int length) {
		return Float.parseFloat(StringCellValueReader.readString(chars, offset, length));
	}
}
