package org.simpleflatmapper.converter.joda.impl;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;


public class CharSequenceToJodaLocalDateConverter implements ContextualConverter<CharSequence, LocalDate> {
    private final DateTimeFormatter dateTimeFormatter;

    public CharSequenceToJodaLocalDateConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public LocalDate convert(CharSequence in, Context context) throws Exception {
        if (in == null || in.length() == 0) return null;
        return dateTimeFormatter.parseLocalDate(String.valueOf(in));
    }
}
