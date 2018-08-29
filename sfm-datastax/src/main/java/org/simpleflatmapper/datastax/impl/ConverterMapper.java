package org.simpleflatmapper.datastax.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.ContextualSourceMapper;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.map.context.MappingContextFactory;

public class ConverterMapper<I, O> implements Converter<I, O> {

    private final ContextualSourceMapper<I, O> mapper;

    public ConverterMapper(ContextualSourceMapper<I, O> mapper) {
        this.mapper = mapper;
    }

    @Override
    public O convert(I in, Context context) throws Exception {
        return mapper.map(in);
    }
}
