package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;

public class DoubleCellValueReader implements CellValueReader<Double> {

	@Override
	public Double read(char[] chars, int offset, int length, ParsingContext parsingContext) {
		return new Double(parseDouble(chars, offset, length));
	}

	public static double parseDouble(char[] chars, int offset, int length) {
		return Double.parseDouble(StringCellValueReader.readString(chars, offset, length));
	}
}
