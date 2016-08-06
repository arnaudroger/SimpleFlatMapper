package org.simpleflatmapper.map.impl;

import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.util.Enumarable;

public class StaticMapperEnumarable<S, T> implements Enumarable<T> {

    private final Mapper<S, T> mapper;
    private final MappingContext<? super S> mappingContext;


    private final Enumarable<S> sourceEnumarable;

    public StaticMapperEnumarable(Mapper<S, T> mapper,
                                  MappingContext<? super S> mappingContext,
                                  Enumarable<S> sourceEnumarable) {
        this.mapper = mapper;
        this.mappingContext = mappingContext;
        this.sourceEnumarable = sourceEnumarable;
    }

    @Override
    public boolean next() {
        return sourceEnumarable.next();
    }

    @Override
    public T currentValue() {
        return mapper.map(sourceEnumarable.currentValue(), mappingContext);
    }

    @Override
    public String toString() {
        return "StaticMapperEnumarable{" +
                "jdbcMapper=" + mapper +
                '}';
    }
}
