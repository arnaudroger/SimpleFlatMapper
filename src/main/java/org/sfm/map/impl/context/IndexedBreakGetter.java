package org.sfm.map.impl.context;

import org.sfm.map.MappingContext;
import org.sfm.utils.BooleanProvider;

public class IndexedBreakGetter<S> implements BooleanProvider {
    private final MappingContext<S> target;
    private final int index;

    public IndexedBreakGetter(MappingContext<S> target, int index) {
        this.target = target;
        this.index = index;
    }

    @Override
    public boolean getBoolean() {
        return target == null || target.broke(index);
    }
}
