package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;

public class FloatCellValueReader implements CellValueReader<Float> {

	@Override
	public Float read(char[] chars, int offset, int length) {
		return new Float(parseFloat(chars, offset, length));
	}
	
	public static float parseFloat(char[] chars, int offset, int length) {
		return Float.parseFloat(StringCellValueReader.readString(chars, offset, length));
	}
}
