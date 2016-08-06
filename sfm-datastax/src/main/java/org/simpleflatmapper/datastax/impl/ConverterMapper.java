package org.simpleflatmapper.datastax.impl;

import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.converter.Converter;

public class ConverterMapper<I, O> implements Converter<I, O> {

    private final Mapper<I, O> mapper;

    public ConverterMapper(Mapper<I, O> mapper) {
        this.mapper = mapper;
    }

    @Override
    public O convert(I in) throws Exception {
        return mapper.map(in);
    }
}
