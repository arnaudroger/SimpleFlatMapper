package org.sfm.csv.impl;

public interface DelayedCellSetterFactory<T, P> {
	DelayedCellSetter<T, P> newCellSetter();
}
