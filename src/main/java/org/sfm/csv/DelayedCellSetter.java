package org.sfm.csv;

public interface DelayedCellSetter<T, P> {
	DelayedSetter<T, P> set(byte[] bytes, int offset, int length) throws Exception;
}
