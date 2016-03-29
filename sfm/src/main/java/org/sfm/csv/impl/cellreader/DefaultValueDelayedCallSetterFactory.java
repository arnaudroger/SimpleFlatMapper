package org.sfm.csv.impl.cellreader;

import org.sfm.csv.mapper.BreakDetector;
import org.sfm.csv.mapper.CsvMapperCellConsumer;
import org.sfm.csv.mapper.DelayedCellSetter;
import org.sfm.csv.mapper.DelayedCellSetterFactory;
import org.sfm.map.column.DefaultValueProperty;

public class DefaultValueDelayedCallSetterFactory<T, P> implements DelayedCellSetterFactory<T, P> {
    private final DelayedCellSetterFactory<T, P> factory;
    private final DefaultValueProperty defaultValueProperty;

    public DefaultValueDelayedCallSetterFactory(DelayedCellSetterFactory<T, P> factory, DefaultValueProperty defaultValueProperty) {
        this.factory = factory;
        this.defaultValueProperty = defaultValueProperty;
    }

    @Override
    public DelayedCellSetter<T, P> newCellSetter(BreakDetector breakDetector, CsvMapperCellConsumer<?>[] cellHandlers) {
        return new DefaultValueDelayedCellSetter(factory.newCellSetter(breakDetector, cellHandlers), defaultValueProperty);
    }

    @Override
    public boolean hasSetter() {
        return factory.hasSetter();
    }
}
