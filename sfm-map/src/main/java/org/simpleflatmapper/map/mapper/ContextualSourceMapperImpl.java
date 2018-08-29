package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.ContextualSourceFieldMapper;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.map.MappingException;


public class ContextualSourceMapperImpl<S, T> implements ContextualSourceFieldMapper<S, T> {
    private final MappingContextFactory<? super S> mappingContextFactory;
    private final SourceFieldMapper<S, T> delegate;

    public ContextualSourceMapperImpl(MappingContextFactory<? super S> mappingContextFactory, SourceFieldMapper<S, T> delegate) {
        this.mappingContextFactory = mappingContextFactory;
        this.delegate = delegate;
    }

    public MappingContext<? super S> newMappingContext() {
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
        return "ContextualSourceMapperImpl{" +
                "mappingContextFactory=" + mappingContextFactory +
                ", delegate=" + delegate +
                '}';
    }

}
