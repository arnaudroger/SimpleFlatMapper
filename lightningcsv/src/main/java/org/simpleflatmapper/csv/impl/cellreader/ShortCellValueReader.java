package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;

public interface ShortCellValueReader extends CellValueReader<Short> {
    short readShort(char[] chars, int offset, int length, ParsingContext parsingContext);
}
