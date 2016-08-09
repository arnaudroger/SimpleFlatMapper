package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.ParsingContext;

public class ByteCellValueReaderImpl implements ByteCellValueReader {

	@Override
	public Byte read(char[] chars, int offset, int length, ParsingContext parsingContext) {
		if (length == 0) return null;
		return readByte(chars, offset, length, parsingContext);
	}

	@Override
	public byte readByte(char[] chars, int offset, int length, ParsingContext parsingContext) {
		if (length == 0) return 0;
		return Byte.parseByte(String.valueOf(chars, offset, length));
	}

    @Override
    public String toString() {
        return "ByteCellValueReaderImpl{}";
    }
}
