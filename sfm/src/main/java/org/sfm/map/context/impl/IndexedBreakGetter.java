package org.sfm.map.context.impl;

import org.sfm.map.MappingContext;
import org.sfm.utils.BooleanProvider;

public class IndexedBreakGetter implements BooleanProvider {
    private final MappingContext<?> target;
    private final int index;

    public IndexedBreakGetter(MappingContext<?> target, int index) {
        this.target = target;
        this.index = index;
    }

    @Override
    public boolean getBoolean() {
        return target == null || target.broke(index);
    }
}
