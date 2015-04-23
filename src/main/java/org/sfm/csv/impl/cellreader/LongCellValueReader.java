package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;

public interface LongCellValueReader extends CellValueReader<Long> {
    long readLong(CharSequence value, ParsingContext parsingContext);
}
