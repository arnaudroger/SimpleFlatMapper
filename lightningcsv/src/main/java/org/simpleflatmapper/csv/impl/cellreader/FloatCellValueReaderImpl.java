package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.ParsingContext;

public final class FloatCellValueReaderImpl implements FloatCellValueReader {

	@Override
	public Float read(char[] chars, int offset, int length, ParsingContext parsingContext) {
		if (length == 0) return null;
		return readFloat(chars, offset, length, parsingContext);
	}

	@Override
	public float readFloat(char[] chars, int offset, int length, ParsingContext parsingContext) {
		return parseFloat(chars, offset, length);
	}
	
	public static float parseFloat(char[] chars, int offset, int length) {
        if (length == 0) return Float.NaN;
		return Float.parseFloat(new String(chars, offset, length));
	}

    @Override
    public String toString() {
        return "FloatCellValueReaderImpl{}";
    }
}
