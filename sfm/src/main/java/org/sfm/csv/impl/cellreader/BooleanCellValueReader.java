package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.ParsingContext;

public interface BooleanCellValueReader extends CellValueReader<Boolean> {
    boolean readBoolean(char[] chars, int offset, int length, ParsingContext parsingContext);
}
