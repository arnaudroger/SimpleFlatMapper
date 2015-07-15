package org.sfm.csv;

import org.sfm.csv.impl.ParsingContext;

public interface CellValueReader<T> {
	T read(char[] chars, int offset, int length, ParsingContext parsingContext);
}
