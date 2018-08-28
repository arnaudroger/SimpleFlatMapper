package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CharSequenceToLocalTimeConverter implements Converter<CharSequence, LocalTime> {

    private final DateTimeFormatter dateTimeFormatter;

    public CharSequenceToLocalTimeConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public LocalTime convert(CharSequence in, Context context) throws Exception {
        if (in == null || in.length() == 0) return null;
        return LocalTime.parse(in, dateTimeFormatter);
    }
}
