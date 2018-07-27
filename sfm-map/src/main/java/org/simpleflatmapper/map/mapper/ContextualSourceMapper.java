package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.map.MappingException;


public class ContextualSourceMapper<S, T> implements SourceMapper<S, T> {
    private final MappingContextFactory<S> mappingContextFactory;
    private final SourceMapper<S, T> delegate;

    public ContextualSourceMapper(SourceMapper<S, T> delegate, MappingContextFactory<S> mappingContextFactory) {
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
    public String toString() {
        return "ContextualSourceMapper{" +
                "mappingContextFactory=" + mappingContextFactory +
                ", delegate=" + delegate +
                '}';
    }
}
