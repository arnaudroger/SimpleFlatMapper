package org.sfm.csv;

public interface DelayedSetter<T, P> {
	public P getValue();
	public void set(T t) throws Exception;
	public boolean isSettable();
}
