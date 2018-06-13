package org.simpleflatmapper.map.impl;

import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.util.ErrorHelper;

public final class GetterMapper<S, P> implements SourceFieldMapper<S, P> {
    private final Getter<? super S, P> getter;

    public GetterMapper(Getter<? super S, P> getter) {
        this.getter = getter;
    }

    @Override
    public P map(S source) throws MappingException {
        try {
            return getter.get(source);
        } catch (Exception e) {
            return ErrorHelper.rethrow(e);
        }
    }

    @Override
    public P map(S source, MappingContext<? super S> context) throws MappingException {
        try {
            return getter.get(source);
        } catch (Exception e) {
            return ErrorHelper.rethrow(e);
        }
    }

    @Override
    public void mapTo(S source, P target, MappingContext<? super S> context) throws Exception {
    }
}
