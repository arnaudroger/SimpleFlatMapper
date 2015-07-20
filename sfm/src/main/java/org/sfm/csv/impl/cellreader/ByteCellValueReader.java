package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.ParsingContext;

public interface ByteCellValueReader extends CellValueReader<Byte> {
    byte readByte(char[] chars, int offset, int length, ParsingContext parsingContext);
}
