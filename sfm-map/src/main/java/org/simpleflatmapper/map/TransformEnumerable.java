package org.simpleflatmapper.map;

import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.Function;

public class TransformEnumerable<I, O> implements Enumerable<O> {

    private final Enumerable<I> delegate;
    private final Function<? super I, ? extends  O> transformer;


    public TransformEnumerable(Enumerable<I> delegate, Function<? super I, ? extends  O> transformer) {
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
        return "TransformEnumerable{" +
                "delegate=" + delegate +
                ", transformer=" + transformer +
                '}';
    }
}
