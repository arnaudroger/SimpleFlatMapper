package org.sfm.csv.impl.cellreader;

import org.sfm.csv.ParsingContext;
import org.sfm.csv.mapper.DelayedCellSetter;
import org.sfm.map.column.DefaultValueProperty;

public class DefaultValueDelayedCellSetter<T, P> implements DelayedCellSetter<T, P> {
    private final DelayedCellSetter<T, P> delegate;
    private final DefaultValueProperty defaultValueProperty;

    private boolean isSet = false;

    public DefaultValueDelayedCellSetter(DelayedCellSetter<T, P> delegate, DefaultValueProperty defaultValueProperty) {
        this.delegate = delegate;
        this.defaultValueProperty = defaultValueProperty;
    }

    @Override
    public void set(char[] chars, int offset, int length, ParsingContext parsingContext) throws Exception {
        isSet = true;
        delegate.set(chars, offset, length, parsingContext);
    }

    @Override
    public P consumeValue() {
        P p;
        if (isSet) {
            p = delegate.consumeValue();
            isSet = false;
        } else {
            p = defaultValue();
        }
        return p;
    }

    @SuppressWarnings("unchecked")
    private P defaultValue() {
        return (P) defaultValueProperty.getValue();
    }

    @Override
    public P peekValue() {
        return isSet ? delegate.peekValue() : defaultValue();
    }

    @Override
    public void set(T t) throws Exception {
        delegate.set(t);
    }

    @Override
    public boolean isSettable() {
        return delegate.isSettable();
    }
}
