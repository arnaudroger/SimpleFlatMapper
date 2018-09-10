package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.converter.Context;

public interface IntContextualGetter<S> {
    int getInt(S s, Context mappingContext) throws Exception;
}
