package org.simpleflatmapper.map.context.impl;

import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.util.BooleanProvider;

public class RootBreakGetterProvider<S> implements Getter<MappingContext<? super S>, BooleanProvider> {

    @Override
    public BooleanProvider get(MappingContext<? super S> target) throws Exception {
        return new RootBreakGetter(target);
    }
}
