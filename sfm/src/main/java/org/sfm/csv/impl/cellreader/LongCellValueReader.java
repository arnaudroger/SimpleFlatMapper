package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.ParsingContext;

public interface LongCellValueReader extends CellValueReader<Long> {
    long readLong(char[] chars, int offset, int length, ParsingContext parsingContext);
}
