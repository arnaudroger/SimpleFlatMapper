package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class CharSequenceToZonedDateTimeConverter implements Converter<CharSequence, ZonedDateTime> {

    private final DateTimeFormatter dateTimeFormatter;

    public CharSequenceToZonedDateTimeConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public ZonedDateTime convert(CharSequence in, Context context) throws Exception {
        if (in == null || in.length() == 0) return null;
        return ZonedDateTime.parse(in, dateTimeFormatter);
    }
}
