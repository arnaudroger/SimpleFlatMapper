package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.converter.Context;

public interface DoubleContextualGetter<S> {
    double getDouble(S s, Context mappingContext) throws Exception;
}
