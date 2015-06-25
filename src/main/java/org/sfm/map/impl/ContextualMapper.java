package org.sfm.map.impl;

import org.sfm.map.Mapper;
import org.sfm.map.MappingContext;
import org.sfm.map.MappingContextFactory;
import org.sfm.map.MappingException;


public class ContextualMapper<S, T> implements Mapper<S, T> {
    private final MappingContextFactory<S> mappingContextFactory;
    private final Mapper<S, T> delegate;

    public ContextualMapper(Mapper<S, T> delegate, MappingContextFactory<S> mappingContextFactory) {
        this.delegate = delegate;
        this.mappingContextFactory = mappingContextFactory;
    }

    public MappingContext<S> newMappingContext() {
        return mappingContextFactory.newContext();
    }

    @Override
    public T map(S source) throws MappingException {
        return delegate.map(source, mappingContextFactory.newContext());
    }

    @Override
    public T map(S source, MappingContext<S> context) throws MappingException {
        return delegate.map(source, context);
    }

    @Override
    public void mapTo(S source, T target, MappingContext<S> context) throws Exception {
        delegate.mapTo(source, target, context);
    }
}
