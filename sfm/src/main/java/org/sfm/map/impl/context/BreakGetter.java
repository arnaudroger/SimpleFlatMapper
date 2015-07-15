package org.sfm.map.impl.context;

import org.sfm.map.MappingContext;
import org.sfm.reflect.Getter;
import org.sfm.utils.BooleanProvider;

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
