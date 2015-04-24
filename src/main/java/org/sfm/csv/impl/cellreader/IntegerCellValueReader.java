package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;

public interface IntegerCellValueReader extends CellValueReader<Integer> {
    int readInt(char[] chars, int offset, int length, ParsingContext parsingContext);
}
