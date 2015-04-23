package org.sfm.csv.impl.cellreader;

import org.sfm.csv.impl.ParsingContext;

public class ByteCellValueReaderImpl implements ByteCellValueReader {

	@Override
	public Byte read(CharSequence value, ParsingContext parsingContext) {
		if (value.length() == 0) return null;
		return readByte(value, parsingContext);
	}

	@Override
	public byte readByte(CharSequence value, ParsingContext parsingContext) {
		return (byte) IntegerCellValueReaderImpl.parseInt(value);
	}

    @Override
    public String toString() {
        return "ByteCellValueReaderImpl{}";
    }
}
