package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Converter;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class CharSequenceToLocalTimeConverter implements Converter<CharSequence, LocalTime> {

    private final DateTimeFormatter dateTimeFormatter;

    public CharSequenceToLocalTimeConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public LocalTime convert(CharSequence in) throws Exception {
        if (in == null || in.length() == 0) return null;
        return LocalTime.parse(in, dateTimeFormatter);
    }
}
