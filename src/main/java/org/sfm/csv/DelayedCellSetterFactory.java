package org.sfm.csv;

public interface DelayedCellSetterFactory<T, P> {
	DelayedCellSetter<T, P> newCellSetter();
}
