package org.sfm.csv.impl;

public interface CellSetter<T> {
	void set(T target, CharSequence value, ParsingContext parsingContext) throws Exception;
}
