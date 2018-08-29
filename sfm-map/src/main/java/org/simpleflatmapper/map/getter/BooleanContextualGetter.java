package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.converter.Context;

public interface BooleanContextualGetter<S> {
    boolean getBoolean(S s, Context mappingContext) throws Exception;
}
