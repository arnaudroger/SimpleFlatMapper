package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.util.Function;

public class TransformSourceFieldMapper<S, I, O> implements SourceFieldMapper<S, O> {
    
    public final SourceFieldMapper<S, I> delegate;
    public final FieldMapper<S, O>[] mappers;
    public final Function<I, O> transform;

    public TransformSourceFieldMapper(SourceFieldMapper<S, I> delegate, FieldMapper<S, O>[] mappers, Function<I, O> transform) {
        this.delegate = delegate;
        this.mappers = mappers;
        this.transform = transform;
    }

    @Override
    public void mapTo(S source, O target, MappingContext<? super S> context) throws Exception {
        for(FieldMapper<S, O> mapper : mappers) {
            mapper.mapTo(source, target, context);
        }
    }

    @Override
    public O map(S source, MappingContext<? super S> context) throws MappingException {
        return transform.apply(delegate.map(source, context));
    }
}
