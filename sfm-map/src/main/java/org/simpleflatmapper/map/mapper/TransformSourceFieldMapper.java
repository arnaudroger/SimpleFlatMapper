package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.util.Function;

public class TransformSourceFieldMapper<S, I, O> implements SourceFieldMapper<S, O> {
    
    public final SourceFieldMapper<S, I> delegate;
    public final Function<I, O> transform;

    public TransformSourceFieldMapper(SourceFieldMapper<S, I> delegate, Function<I, O> transform) {
        this.delegate = delegate;
        this.transform = transform;
    }

    @Override
    public void mapTo(S source, O target, MappingContext<? super S> context) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public O map(S source) throws MappingException {
        return transform.apply(delegate.map(source));
    }

    @Override
    public O map(S source, MappingContext<? super S> context) throws MappingException {
        return transform.apply(delegate.map(source, context));
    }
}
