package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;

public interface DoubleCellValueReader extends CellValueReader<Double> {
    double readDouble(char[] chars, int offset, int length, ParsingContext parsingContext);
}
