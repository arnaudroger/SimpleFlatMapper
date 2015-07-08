package org.sfm.map.impl;


import org.sfm.map.Mapper;
import org.sfm.map.MappingContext;
import org.sfm.map.MappingException;

public abstract class AbstractDelegateMapper<S, T> implements Mapper<S, T> {
    @Override
    public final T map(S source) throws MappingException {
        return getMapper(source).map(source);
    }

    @Override
    public final T map(S source, MappingContext<? super S> context) throws MappingException {
        return getMapper(source).map(source, context);
    }

    @Override
    public final void mapTo(S source, T target, MappingContext<? super S> context) throws Exception {
        getMapper(source).mapTo(source, target, context);
    }

    protected abstract Mapper<S, T> getMapper(S source);

}
