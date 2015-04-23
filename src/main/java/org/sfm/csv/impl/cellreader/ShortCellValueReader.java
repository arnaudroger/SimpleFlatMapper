package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;

public interface ShortCellValueReader extends CellValueReader<Short> {
    short readShort(CharSequence value, ParsingContext parsingContext);
}
