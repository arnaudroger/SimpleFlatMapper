package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.ContextualSourceFieldMapper;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.map.MappingException;

import static org.simpleflatmapper.util.Asserts.requireNonNull;


public class ContextualSourceFieldMapperImpl<S, T> implements ContextualSourceFieldMapper<S, T> {
    private final MappingContextFactory<? super S> mappingContextFactory;
    private final SourceFieldMapper<S, T> delegate;

    public ContextualSourceFieldMapperImpl(MappingContextFactory<? super S> mappingContextFactory, SourceFieldMapper<S, T> delegate) {
        this.mappingContextFactory = requireNonNull("mappingContextFactory", mappingContextFactory);
        this.delegate = requireNonNull("delegate", delegate);
    }

    public SourceFieldMapper<S, T> getDelegate() {
        return delegate;
    }

    public MappingContext<? super S> newMappingContext() {
        return mappingContextFactory.newContext();
    }

    @Override
    public T map(S source) throws MappingException {
        MappingContext<? super S> context = mappingContextFactory.newContext();
        context.handleSource(source);
        return delegate.map(source, context);
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

    public MappingContextFactory<? super S> getMappingContextFactory() {
        return mappingContextFactory;
    }
}
