package org.simpleflatmapper.map.impl;

import org.simpleflatmapper.util.Enumarable;
import org.simpleflatmapper.util.Function;

public class TransformEnumarable<I, O> implements Enumarable<O> {

    private final Enumarable<I> delegate;
    private final Function<I, O> transformer;


    public TransformEnumarable(Enumarable<I> delegate, Function<I, O> transformer) {
        this.delegate = delegate;
        this.transformer = transformer;
    }

    @Override
    public boolean next() {
        return delegate.next();
    }

    @Override
    public O currentValue() {
        return transformer.apply(delegate.currentValue());
    }

    @Override
    public String toString() {
        return "TransformEnumarable{" +
                "delegate=" + delegate +
                ", transformer=" + transformer +
                '}';
    }
}
