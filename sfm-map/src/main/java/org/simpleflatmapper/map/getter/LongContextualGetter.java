package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.converter.Context;

public interface LongContextualGetter<S> {
    long getLong(S s, Context mappingContext) throws Exception;
}
