package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class CharSequenceToInstantConverter implements Converter<CharSequence, Instant> {

    private final DateTimeFormatter dateTimeFormatter;

    public CharSequenceToInstantConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public Instant convert(CharSequence in, Context context) throws Exception {
        if (in == null || in.length() == 0) return null;
        return dateTimeFormatter.parse(in, Instant::from);
    }
}
