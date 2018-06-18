package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;

public interface ByteCellValueReader extends CellValueReader<Byte> {
    byte readByte(char[] chars, int offset, int length, ParsingContext parsingContext);
}
