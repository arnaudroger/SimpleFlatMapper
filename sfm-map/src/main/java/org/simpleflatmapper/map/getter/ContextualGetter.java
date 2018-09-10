package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.converter.Context;

public interface ContextualGetter<S, T> {
    T get(S s, Context context) throws Exception;
}
