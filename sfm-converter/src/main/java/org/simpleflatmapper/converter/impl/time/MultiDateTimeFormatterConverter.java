package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;

import java.time.format.DateTimeParseException;

public class MultiDateTimeFormatterConverter<I, O> implements Converter<I, O> {
    private final Converter<I, O>[] converters;

    public MultiDateTimeFormatterConverter(Converter<I, O>[] converters) {
        this.converters = converters;
    }

    @Override
    public O convert(I in, Context context) throws Exception {
        for(int i = converters.length - 1; i >= 0; i--) {
            Converter<I, O> converter = converters[i];
            try {
                return converter.convert(in, context);
            } catch (DateTimeParseException e) {
                // ignore
            }
        }
        throw new DateTimeParseException("Unable to parse " + in,  String.valueOf(in), 0);
    }
}
