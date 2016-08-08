package org.simpleflatmapper.converter.joda.impl;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.simpleflatmapper.converter.Converter;


public class CharacterSequenceToJodaDateTimeConverter implements Converter<CharSequence, DateTime> {
    private final DateTimeFormatter dateTimeFormatter;

    public CharacterSequenceToJodaDateTimeConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public DateTime convert(CharSequence in) throws Exception {
        if (in == null || in.length() == 0) return null;
        return dateTimeFormatter.parseDateTime(String.valueOf(in));
    }
}
