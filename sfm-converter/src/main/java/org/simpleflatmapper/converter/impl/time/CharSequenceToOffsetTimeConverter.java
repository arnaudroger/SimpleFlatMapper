package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Converter;

import java.time.Instant;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class CharSequenceToOffsetTimeConverter implements Converter<CharSequence, OffsetTime> {

    private final DateTimeFormatter dateTimeFormatter;

    public CharSequenceToOffsetTimeConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public OffsetTime convert(CharSequence in) throws Exception {
        if (in == null || in.length() == 0) return null;
        return OffsetTime.parse(in, dateTimeFormatter);
    }
}
