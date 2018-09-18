package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CharSequenceToLocalDateTimeConverter implements ContextualConverter<CharSequence, LocalDateTime> {

    private final DateTimeFormatter dateTimeFormatter;

    public CharSequenceToLocalDateTimeConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public LocalDateTime convert(CharSequence in, Context context) throws Exception {
        if (in == null || in.length() == 0) return null;
        return LocalDateTime.parse(in, dateTimeFormatter);
    }
}
