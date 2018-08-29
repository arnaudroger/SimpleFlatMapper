package org.simpleflatmapper.map.getter;

import org.simpleflatmapper.converter.Context;

public interface ByteContextualGetter<S> {
    byte getByte(S s, Context mappingContext) throws Exception;
}
