package org.sfm.csv;

import org.sfm.csv.impl.ParsingContext;

public interface CellValueReader<T> {
	T read(CharSequence value, ParsingContext parsingContext);
}
