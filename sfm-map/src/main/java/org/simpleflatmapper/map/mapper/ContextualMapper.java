package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.map.MappingException;


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
    public T map(S source, MappingContext<? super S> context) throws MappingException {
        return delegate.map(source, context);
    }

    @Override
    public void mapTo(S source, T target, MappingContext<? super S> context) throws Exception {
        delegate.mapTo(source, target, context);
    }

    @Override
    public String toString() {
        return "ContextualMapper{" +
                "mappingContextFactory=" + mappingContextFactory +
                ", delegate=" + delegate +
                '}';
    }
}
