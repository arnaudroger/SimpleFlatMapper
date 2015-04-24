package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;

public interface FloatCellValueReader extends CellValueReader<Float> {
    float readFloat(char[] chars, int offset, int length, ParsingContext parsingContext);
}
