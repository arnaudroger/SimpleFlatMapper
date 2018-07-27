package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.context.MappingContextFactory;

public class ContextualFieldMapper<S, T> implements FieldMapper<S, T> {
    private final MappingContextFactory<S> mappingContextFactory;
    private final FieldMapper<S, T> delegate;

    public ContextualFieldMapper(FieldMapper<S, T> delegate, MappingContextFactory<S> mappingContextFactory) {
        this.delegate = delegate;
        this.mappingContextFactory = mappingContextFactory;
    }

    public MappingContext<S> newMappingContext() {
        return mappingContextFactory.newContext();
    }

    @Override
    public String toString() {
        return "ContextualFieldMapper{" +
                "mappingContextFactory=" + mappingContextFactory +
                ", delegate=" + delegate +
                '}';
    }

    @Override
    public void mapTo(S source, T target, MappingContext<? super S> context) throws Exception {
        delegate.mapTo(source, target, context);
    }
}
