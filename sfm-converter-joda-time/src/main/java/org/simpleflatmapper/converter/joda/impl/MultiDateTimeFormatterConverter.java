package org.simpleflatmapper.converter.joda.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

public class MultiDateTimeFormatterConverter<I, O> implements ContextualConverter<I, O> {
    private final ContextualConverter<I, O>[] converters;

    public MultiDateTimeFormatterConverter(ContextualConverter<I, O>[] converters) {
        this.converters = converters;
    }

    @Override
    public O convert(I in, Context context) throws Exception {
        for(int i = converters.length - 1; i >= 0; i--) {
            ContextualConverter<I, O> converter = converters[i];
            try {
                return converter.convert(in, context );
            } catch (IllegalArgumentException e) {
                // ignore
            }
        }
        throw new IllegalArgumentException("Unable to parse " + in);
    }
}
