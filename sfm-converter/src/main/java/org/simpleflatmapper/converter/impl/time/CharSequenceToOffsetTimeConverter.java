package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;

public class CharSequenceToOffsetTimeConverter implements ContextualConverter<CharSequence, OffsetTime> {

    private final DateTimeFormatter dateTimeFormatter;

    public CharSequenceToOffsetTimeConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public OffsetTime convert(CharSequence in, Context context) throws Exception {
        if (in == null || in.length() == 0) return null;
        return OffsetTime.parse(in, dateTimeFormatter);
    }
}
