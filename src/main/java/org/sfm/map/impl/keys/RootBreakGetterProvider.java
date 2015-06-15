package org.sfm.map.impl.keys;

import org.sfm.map.MappingContext;
import org.sfm.map.impl.MappingContextFactoryBuilder;
import org.sfm.reflect.Getter;
import org.sfm.utils.BooleanProvider;

public class RootBreakGetterProvider<S> implements Getter<MappingContext<S>, BooleanProvider> {

    @Override
    public BooleanProvider get(MappingContext<S> target) throws Exception {
        return new RootBreakGetter<S>(target);
    }
}
