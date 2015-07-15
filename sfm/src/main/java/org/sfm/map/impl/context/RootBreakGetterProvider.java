package org.sfm.map.impl.context;

import org.sfm.map.MappingContext;
import org.sfm.reflect.Getter;
import org.sfm.utils.BooleanProvider;

public class RootBreakGetterProvider<S> implements Getter<MappingContext<? super S>, BooleanProvider> {

    @Override
    public BooleanProvider get(MappingContext<? super S> target) throws Exception {
        return new RootBreakGetter(target);
    }
}
