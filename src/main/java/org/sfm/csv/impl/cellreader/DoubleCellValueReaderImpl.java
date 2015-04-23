package org.sfm.csv.impl.cellreader;

import org.sfm.csv.impl.ParsingContext;

public final class DoubleCellValueReaderImpl implements DoubleCellValueReader {

	@Override
	public Double read(CharSequence value, ParsingContext parsingContext) {
		if (value.length() == 0) return null;
		return readDouble(value, parsingContext);
	}

	@Override
	public double readDouble(CharSequence value, ParsingContext parsingContext) {
		return parseDouble(value);
	}
	public static double parseDouble(CharSequence value) {
        if (value.length() == 0) return Double.NaN;
        return Double.parseDouble(StringCellValueReader.readString(value));
	}

    @Override
    public String toString() {
        return "DoubleCellValueReaderImpl{}";
    }
}
