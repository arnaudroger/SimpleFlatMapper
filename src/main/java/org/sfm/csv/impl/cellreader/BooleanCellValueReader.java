package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;

public interface BooleanCellValueReader extends CellValueReader<Boolean> {
    boolean readBoolean(CharSequence value, ParsingContext parsingContext);
}
