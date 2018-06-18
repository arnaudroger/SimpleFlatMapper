package org.simpleflatmapper.csv.impl.cellreader;

import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.mapper.DelayedCellSetter;
import org.simpleflatmapper.map.property.DefaultValueProperty;
import org.simpleflatmapper.reflect.Setter;

public class DefaultValueDelayedCellSetter<T, P>
        implements DelayedCellSetter<T, P> {
    private final DelayedCellSetter<T, P> delegate;
    private final DefaultValueProperty defaultValueProperty;
    private final Setter<? super T, ? super P> setter;

    private boolean isSet = false;

    public DefaultValueDelayedCellSetter(DelayedCellSetter<T, P> delegate,
                                         DefaultValueProperty defaultValueProperty,
                                         Setter<? super T, ? super P> setter) {
        this.delegate = delegate;
        this.defaultValueProperty = defaultValueProperty;
        this.setter = setter;
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
        P val = consumeValue();
        setter.set(t, val);
    }

    @Override
    public boolean isSettable() {
        return delegate.isSettable();
    }
}
