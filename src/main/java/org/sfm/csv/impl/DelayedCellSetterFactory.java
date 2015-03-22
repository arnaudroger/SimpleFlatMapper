package org.sfm.csv.impl;

public interface DelayedCellSetterFactory<T, P> {
	DelayedCellSetter<T, P> newCellSetter(BreakDetector breakDectector, CsvMapperCellConsumer<?>[]cellHandlers);
    boolean hasSetter();
}
