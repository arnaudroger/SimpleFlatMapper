package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.format.DateTimeParseException;

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
                return converter.convert(in, context);
            } catch (DateTimeParseException e) {
                // ignore
            }
        }
        throw new DateTimeParseException("Unable to parse " + in,  String.valueOf(in), 0);
    }
}
