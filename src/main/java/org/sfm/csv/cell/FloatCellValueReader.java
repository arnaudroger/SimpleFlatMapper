package org.sfm.csv.cell;

import org.sfm.csv.CellValueReader;

public class FloatCellValueReader implements CellValueReader<Float> {

	@Override
	public Float read(byte[] bytes, int offset, int length) {
		return new Float(parseFloat(bytes, offset, length));
	}
	
	public static float parseFloat(byte[] bytes, int offset, int length) {
		return Float.parseFloat(StringCellValueReader.readString(bytes, offset, length));
	}

}
