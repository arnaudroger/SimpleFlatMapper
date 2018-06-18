package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.ParsingContext;


public interface CharCellValueReader extends CellValueReader<Character> {
    char readChar(char[] bytes, int offset, int length, ParsingContext parsingContext);
}
