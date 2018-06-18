package org.simpleflatmapper.csv;

public interface CellValueReader<T> {
	T read(char[] chars, int offset, int length, ParsingContext parsingContext);
}
