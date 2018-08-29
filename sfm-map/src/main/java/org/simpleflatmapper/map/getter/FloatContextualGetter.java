package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.converter.Context;

public interface FloatContextualGetter<S> {
    float getFloat(S s, Context mappingContext) throws Exception;
}
