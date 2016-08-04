package org.simpleflatmapper.core.map.context.impl;

import org.simpleflatmapper.core.map.MappingContext;
import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.util.BooleanProvider;

public class RootBreakGetterProvider<S> implements Getter<MappingContext<? super S>, BooleanProvider> {

    @Override
    public BooleanProvider get(MappingContext<? super S> target) throws Exception {
        return new RootBreakGetter(target);
    }
}
