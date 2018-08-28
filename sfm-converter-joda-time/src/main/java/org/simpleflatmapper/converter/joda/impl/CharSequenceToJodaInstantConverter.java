package org.simpleflatmapper.converter.joda.impl;

import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;


public class CharSequenceToJodaInstantConverter implements Converter<CharSequence, Instant> {
    private final DateTimeFormatter dateTimeFormatter;

    public CharSequenceToJodaInstantConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public Instant convert(CharSequence in, Context context) throws Exception {
        if (in == null || in.length() == 0) return null;
        return dateTimeFormatter.parseDateTime(String.valueOf(in)).toInstant();
    }
}
