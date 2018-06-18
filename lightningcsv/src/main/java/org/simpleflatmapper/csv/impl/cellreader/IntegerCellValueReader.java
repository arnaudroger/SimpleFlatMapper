package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;

public interface IntegerCellValueReader extends CellValueReader<Integer> {
    int readInt(char[] chars, int offset, int length, ParsingContext parsingContext);
}
