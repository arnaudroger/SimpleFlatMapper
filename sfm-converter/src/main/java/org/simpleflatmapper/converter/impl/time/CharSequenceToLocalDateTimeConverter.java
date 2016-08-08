package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Converter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class CharSequenceToLocalDateTimeConverter implements Converter<CharSequence, LocalDateTime> {

    private final DateTimeFormatter dateTimeFormatter;

    public CharSequenceToLocalDateTimeConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public LocalDateTime convert(CharSequence in) throws Exception {
        if (in == null || in.length() == 0) return null;
        return LocalDateTime.parse(in, dateTimeFormatter);
    }
}
