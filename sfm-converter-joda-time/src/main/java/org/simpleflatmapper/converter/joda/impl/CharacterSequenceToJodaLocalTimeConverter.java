package org.simpleflatmapper.converter.joda.impl;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.simpleflatmapper.converter.Converter;


public class CharacterSequenceToJodaLocalTimeConverter implements Converter<CharSequence, LocalTime> {
    private final DateTimeFormatter dateTimeFormatter;

    public CharacterSequenceToJodaLocalTimeConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public LocalTime convert(CharSequence in) throws Exception {
        if (in == null || in.length() == 0) return null;
        return dateTimeFormatter.parseLocalTime(String.valueOf(in));
    }
}
