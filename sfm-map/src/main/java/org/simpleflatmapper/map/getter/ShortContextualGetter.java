package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.converter.Context;

public interface ShortContextualGetter<S> {
    short getShort(S s, Context mappingContext) throws Exception;
}
