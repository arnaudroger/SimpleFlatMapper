package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.ParsingContext;

public interface ShortCellValueReader extends CellValueReader<Short> {
    short readShort(char[] chars, int offset, int length, ParsingContext parsingContext);
}
