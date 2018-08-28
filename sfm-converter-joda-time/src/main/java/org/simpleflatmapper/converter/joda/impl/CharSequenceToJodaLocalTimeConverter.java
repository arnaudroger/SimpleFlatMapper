package org.simpleflatmapper.converter.joda.impl;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;


public class CharSequenceToJodaLocalTimeConverter implements Converter<CharSequence, LocalTime> {
    private final DateTimeFormatter dateTimeFormatter;

    public CharSequenceToJodaLocalTimeConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public LocalTime convert(CharSequence in, Context context) throws Exception {
        if (in == null || in.length() == 0) return null;
        return dateTimeFormatter.parseLocalTime(String.valueOf(in));
    }
}
