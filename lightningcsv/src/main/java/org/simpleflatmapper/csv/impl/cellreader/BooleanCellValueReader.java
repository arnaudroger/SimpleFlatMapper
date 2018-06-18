package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;

public interface BooleanCellValueReader extends CellValueReader<Boolean> {
    boolean readBoolean(char[] chars, int offset, int length, ParsingContext parsingContext);
}
