package org.sfm.csv;

public interface DelayedCellSetter<T, P> {
	
	void set(char[] chars, int offset, int length, ParsingContext parsingContext) throws Exception;

	public P getValue();
	public void set(T t) throws Exception;
	public boolean isSettable();

}
