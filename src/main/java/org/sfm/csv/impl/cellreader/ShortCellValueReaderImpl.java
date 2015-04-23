package org.sfm.csv.impl.cellreader;

import org.sfm.csv.impl.ParsingContext;

public final class ShortCellValueReaderImpl implements ShortCellValueReader {

	@Override
	public Short read(CharSequence value, ParsingContext parsingContext) {
		if (value.length() == 0) return null;
		return readShort(value, parsingContext);
	}

	@Override
	public short readShort(CharSequence value, ParsingContext parsingContext) {
		return (short) IntegerCellValueReaderImpl.parseInt(value);
	}

    @Override
    public String toString() {
        return "ShortCellValueReaderImpl{}";
    }
}
