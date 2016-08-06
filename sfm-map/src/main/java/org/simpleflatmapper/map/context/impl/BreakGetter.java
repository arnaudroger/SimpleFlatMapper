package org.simpleflatmapper.map.context.impl;

import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.util.BooleanProvider;

public class BreakGetter<S> implements Getter<MappingContext<? super S>, BooleanProvider> {
    private final int index;

    public BreakGetter(int index) {
        this.index = index;
    }

    @Override
    public BooleanProvider get(MappingContext<? super S> target) throws Exception {
        return new IndexedBreakGetter(target, index);
    }
}
