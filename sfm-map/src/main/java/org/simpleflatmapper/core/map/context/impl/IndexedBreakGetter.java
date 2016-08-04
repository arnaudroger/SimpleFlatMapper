package org.simpleflatmapper.core.map.context.impl;

import org.simpleflatmapper.core.map.MappingContext;
import org.simpleflatmapper.util.BooleanProvider;

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
