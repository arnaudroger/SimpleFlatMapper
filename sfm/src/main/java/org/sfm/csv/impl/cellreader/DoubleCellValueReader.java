package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.ParsingContext;

public interface DoubleCellValueReader extends CellValueReader<Double> {
    double readDouble(char[] chars, int offset, int length, ParsingContext parsingContext);
}
