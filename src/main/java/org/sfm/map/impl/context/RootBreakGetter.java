package org.sfm.map.impl.context;

import org.sfm.map.MappingContext;
import org.sfm.utils.BooleanProvider;

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
