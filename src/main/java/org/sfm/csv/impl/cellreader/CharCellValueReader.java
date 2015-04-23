package org.sfm.csv.impl.cellreader;

import org.sfm.csv.CellValueReader;
import org.sfm.csv.impl.ParsingContext;


public interface CharCellValueReader extends CellValueReader<Character> {
    char readChar(CharSequence value, ParsingContext parsingContext);
}
