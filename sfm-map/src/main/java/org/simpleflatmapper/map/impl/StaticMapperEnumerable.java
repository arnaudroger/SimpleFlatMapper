package org.simpleflatmapper.map.impl;

import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.util.Enumerable;

public class StaticMapperEnumerable<S, T> implements Enumerable<T> {

    private final SourceMapper<S, T> mapper;
    private final MappingContext<? super S> mappingContext;


    private final Enumerable<S> sourceEnumerable;

    public StaticMapperEnumerable(SourceMapper<S, T> mapper,
                                  MappingContext<? super S> mappingContext,
                                  Enumerable<S> sourceEnumerable) {
        this.mapper = mapper;
        this.mappingContext = mappingContext;
        this.sourceEnumerable = sourceEnumerable;
    }

    @Override
    public boolean next() {
        return sourceEnumerable.next();
    }

    @Override
    public T currentValue() {
        return mapper.map(sourceEnumerable.currentValue(), mappingContext);
    }

    @Override
    public String toString() {
        return "StaticMapperEnumerable{" +
                "jdbcMapper=" + mapper +
                '}';
    }
}
