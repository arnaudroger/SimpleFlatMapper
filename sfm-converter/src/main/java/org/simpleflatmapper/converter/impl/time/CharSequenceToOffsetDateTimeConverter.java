package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class CharSequenceToOffsetDateTimeConverter implements ContextualConverter<CharSequence, OffsetDateTime> {

    private final DateTimeFormatter dateTimeFormatter;

    public CharSequenceToOffsetDateTimeConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public OffsetDateTime convert(CharSequence in, Context context) throws Exception {
        if (in == null || in.length() == 0) return null;
        return OffsetDateTime.parse(in, dateTimeFormatter);
    }
}
