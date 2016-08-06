package org.simpleflatmapper.map.context.impl;

import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.util.BooleanProvider;

public class RootBreakGetter implements BooleanProvider {
    private final MappingContext<?> target;

    public RootBreakGetter(MappingContext<?> target) {
        this.target = target;
    }

    @Override
    public boolean getBoolean() {
        return target == null || target.rootBroke();
    }
}
