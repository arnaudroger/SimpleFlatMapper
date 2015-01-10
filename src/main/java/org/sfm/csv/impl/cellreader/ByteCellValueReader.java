package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;

public interface ByteCellValueReader extends CellValueReader<Byte> {
    byte readByte(char[] chars, int offset, int length, ParsingContext parsingContext);
}
