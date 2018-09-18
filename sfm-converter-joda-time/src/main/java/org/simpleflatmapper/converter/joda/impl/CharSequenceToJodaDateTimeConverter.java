package org.simpleflatmapper.converter.joda.impl;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;


public class CharSequenceToJodaDateTimeConverter implements ContextualConverter<CharSequence, DateTime> {
    private final DateTimeFormatter dateTimeFormatter;

    public CharSequenceToJodaDateTimeConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public DateTime convert(CharSequence in, Context context) throws Exception {
        if (in == null || in.length() == 0) return null;
        return dateTimeFormatter.parseDateTime(String.valueOf(in));
    }
}
