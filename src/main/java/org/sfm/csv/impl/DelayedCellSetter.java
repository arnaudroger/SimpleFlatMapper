package org.sfm.csv.impl;

public interface DelayedCellSetter<T, P> {
	
	void set(char[] chars, int offset, int length, ParsingContext parsingContext) throws Exception;

	public P consumeValue();
    public P peekValue();
	public void set(T t) throws Exception;
	public boolean isSettable();

}
