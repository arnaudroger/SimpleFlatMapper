package org.sfm.map.impl.keys;

import org.sfm.map.MappingContext;
import org.sfm.utils.BooleanProvider;

public class RootBreakGetter<S> implements BooleanProvider {
    private final MappingContext<S> target;

    public RootBreakGetter(MappingContext<S> target) {
        this.target = target;
    }

    @Override
    public boolean getBoolean() {
        return target == null || target.rootBroke();
    }
}
