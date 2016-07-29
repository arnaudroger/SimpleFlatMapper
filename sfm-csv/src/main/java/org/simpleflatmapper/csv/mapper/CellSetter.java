package org.simpleflatmapper.csv.mapper;

import org.simpleflatmapper.csv.ParsingContext;

public interface CellSetter<T> {
	void set(T target, char[] chars, int offset, int length, ParsingContext parsingContext) throws Exception;
}
