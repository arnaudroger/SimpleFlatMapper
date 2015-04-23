package org.sfm.csv.impl;

public interface DelayedCellSetter<T, P> {
	
	void set(CharSequence value, ParsingContext parsingContext) throws Exception;

	P consumeValue();
    P peekValue();
	void set(T t) throws Exception;
	boolean isSettable();

}
