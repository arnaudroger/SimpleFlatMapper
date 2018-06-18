package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;

public interface LongCellValueReader extends CellValueReader<Long> {
    long readLong(char[] chars, int offset, int length, ParsingContext parsingContext);
}
