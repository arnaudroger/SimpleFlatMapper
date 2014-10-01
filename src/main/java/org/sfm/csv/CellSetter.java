package org.sfm.csv;

public interface CellSetter<T> {
	void set(T target, char[] chars, int offset, int length, ParsingContext parsingContext) throws Exception;
}
