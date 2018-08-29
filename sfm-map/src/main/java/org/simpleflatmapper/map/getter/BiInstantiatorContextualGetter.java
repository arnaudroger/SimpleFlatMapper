package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.reflect.BiInstantiator;

public class BiInstantiatorContextualGetter<S, T> implements ContextualGetter<S, T> {
    private final BiInstantiator<? super S, ? super Context, ? extends T> biInstantiator;

    public BiInstantiatorContextualGetter(BiInstantiator<? super S, ? super Context, ? extends T> biInstantiator) {
        this.biInstantiator = biInstantiator;
    }

    @Override
    public T get(S s, Context context) throws Exception {
        return biInstantiator.newInstance(s, context);
    }
}
