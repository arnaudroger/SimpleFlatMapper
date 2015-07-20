package org.sfm.csv.mapper;


public interface DelayedCellSetterFactory<T, P> {
	DelayedCellSetter<T, P> newCellSetter(BreakDetector breakDetector, CsvMapperCellConsumer<?>[]cellHandlers);
    boolean hasSetter();
}
