package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CharSequenceToLocalDateConverter implements ContextualConverter<CharSequence, LocalDate> {

    private final DateTimeFormatter dateTimeFormatter;

    public CharSequenceToLocalDateConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public LocalDate convert(CharSequence in, Context context) throws Exception {
        if (in == null || in.length() == 0) return null;
        return LocalDate.parse(in, dateTimeFormatter);
    }
}
