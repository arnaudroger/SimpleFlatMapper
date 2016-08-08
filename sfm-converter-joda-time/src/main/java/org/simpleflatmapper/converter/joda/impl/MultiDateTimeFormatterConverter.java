package org.simpleflatmapper.converter.joda.impl;

import org.simpleflatmapper.converter.Converter;

import java.time.format.DateTimeParseException;

public class MultiDateTimeFormatterConverter<I, O> implements Converter<I, O> {
    private final Converter<I, O>[] converters;

    public MultiDateTimeFormatterConverter(Converter<I, O>[] converters) {
        this.converters = converters;
    }

    @Override
    public O convert(I in) throws Exception {
        for(Converter<I, O> converter : converters) {
            try {
                return converter.convert(in);
            } catch (IllegalArgumentException e) {
                // ignore
            }
        }
        throw new IllegalArgumentException("Unable to parse " + in);
    }
}
