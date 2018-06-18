package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;

public interface FloatCellValueReader extends CellValueReader<Float> {
    float readFloat(char[] chars, int offset, int length, ParsingContext parsingContext);
}
