package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;

public interface FloatCellValueReader extends CellValueReader<Float> {
    float readFloat(CharSequence value, ParsingContext parsingContext);
}
