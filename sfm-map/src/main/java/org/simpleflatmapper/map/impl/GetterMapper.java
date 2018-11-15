package org.simpleflatmapper.map.impl;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.util.ErrorHelper;

public final class GetterMapper<S, P> implements SourceMapper<S, P>, FieldMapper<S, P> {
    private final ContextualGetter<? super S, P> getter;

    public GetterMapper(ContextualGetter<? super S, P> getter) {
        this.getter = getter;
    }
    @Override
    public P map(S source, MappingContext<? super S> context) throws MappingException {
        try {
            return getter.get(source, context);
        } catch (Exception e) {
            return ErrorHelper.rethrow(e);
        }
    }

    @Override
    public void mapTo(S source, P target, MappingContext<? super S> context) throws Exception {
        // cannot map to an object
    }
}
