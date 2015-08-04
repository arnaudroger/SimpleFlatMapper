package org.sfm.datastax.impl;

import org.sfm.map.Mapper;
import org.sfm.utils.conv.Converter;

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
