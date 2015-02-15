package org.sfm.csv.impl.cellreader;

import org.sfm.csv.impl.ParsingContext;

public class ByteCellValueReaderImpl implements ByteCellValueReader {

	@Override
	public Byte read(char[] chars, int offset, int length, ParsingContext parsingContext) {
		if (length == 0) return null;
		return new Byte(readByte(chars, offset, length, parsingContext));
	}

	@Override
	public byte readByte(char[] chars, int offset, int length, ParsingContext parsingContext) {
		return (byte) IntegerCellValueReaderImpl.parseInt(chars, offset, length);
	}

    @Override
    public String toString() {
        return "ByteCellValueReaderImpl{}";
    }
}
