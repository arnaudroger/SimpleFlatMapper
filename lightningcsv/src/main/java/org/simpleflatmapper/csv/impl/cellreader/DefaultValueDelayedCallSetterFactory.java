package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.mapper.BreakDetector;
import org.simpleflatmapper.csv.mapper.CsvMapperCellConsumer;
import org.simpleflatmapper.csv.mapper.DelayedCellSetter;
import org.simpleflatmapper.csv.mapper.DelayedCellSetterFactory;
import org.simpleflatmapper.map.property.DefaultValueProperty;
import org.simpleflatmapper.reflect.Setter;

public class DefaultValueDelayedCallSetterFactory<T, P>
        implements DelayedCellSetterFactory<T, P> {
    private final DelayedCellSetterFactory<T, P> factory;
    private final DefaultValueProperty defaultValueProperty;
    private final Setter<? super T, ? super P> setter;

    public DefaultValueDelayedCallSetterFactory(DelayedCellSetterFactory<T, P> factory, DefaultValueProperty defaultValueProperty, Setter<? super T, ? super P> setter) {
        this.factory = factory;
        this.defaultValueProperty = defaultValueProperty;
        this.setter = setter;
    }

    @Override
    public DelayedCellSetter<T, P> newCellSetter(BreakDetector breakDetector, CsvMapperCellConsumer<?>[] cellHandlers) {
        return new DefaultValueDelayedCellSetter<T, P>(factory.newCellSetter(breakDetector, cellHandlers), defaultValueProperty, setter);
    }

    @Override
    public boolean hasSetter() {
        return factory.hasSetter();
    }
}
