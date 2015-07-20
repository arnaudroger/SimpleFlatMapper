package org.sfm.csv.mapper;

import org.sfm.csv.ParsingContext;

public interface DelayedCellSetter<T, P> {
	
	void set(char[] chars, int offset, int length, ParsingContext parsingContext) throws Exception;

	P consumeValue();
    P peekValue();
	void set(T t) throws Exception;
	boolean isSettable();

}
