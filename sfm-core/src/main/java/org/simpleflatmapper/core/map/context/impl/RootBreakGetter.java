package org.simpleflatmapper.core.map.context.impl;

import org.simpleflatmapper.core.map.MappingContext;
import org.simpleflatmapper.core.utils.BooleanProvider;

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
