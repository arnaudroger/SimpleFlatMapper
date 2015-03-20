package org.sfm.csv.impl;

import org.sfm.csv.CsvMapper;

import java.util.Map;

public interface DelayedCellSetterFactory<T, P> {
	DelayedCellSetter<T, P> newCellSetter(BreakDetector breakDectector, Map<CsvMapper<?>, CsvMapperCellConsumer<?>> cellHandlers);
}
