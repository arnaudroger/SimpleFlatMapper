package org.simpleflatmapper.datastax.impl;

import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.converter.Converter;

public class ConverterMapper<I, O> implements Converter<I, O> {

    private final SourceMapper<I, O> mapper;

    public ConverterMapper(SourceMapper<I, O> mapper) {
        this.mapper = mapper;
    }

    @Override
    public O convert(I in) throws Exception {
        return mapper.map(in);
    }
}
