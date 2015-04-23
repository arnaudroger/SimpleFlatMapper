package org.sfm.csv.impl.cellreader;

import org.sfm.csv.impl.ParsingContext;

public final class FloatCellValueReaderImpl implements FloatCellValueReader {

	@Override
	public Float read(CharSequence value, ParsingContext parsingContext) {
		if (value.length() == 0) return null;
		return readFloat(value, parsingContext);
	}

	@Override
	public float readFloat(CharSequence value, ParsingContext parsingContext) {
		return parseFloat(value);
	}
	
	public static float parseFloat(CharSequence value) {
        if (value.length() == 0) return Float.NaN;
		return Float.parseFloat(StringCellValueReader.readString(value));
	}

    @Override
    public String toString() {
        return "FloatCellValueReaderImpl{}";
    }
}
