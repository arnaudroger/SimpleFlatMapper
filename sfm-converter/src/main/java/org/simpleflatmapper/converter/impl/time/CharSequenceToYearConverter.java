package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.Year;
import java.time.format.DateTimeFormatter;

public class CharSequenceToYearConverter implements ContextualConverter<CharSequence, Year> {

    private final DateTimeFormatter dateTimeFormatter;

    public CharSequenceToYearConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public Year convert(CharSequence in, Context context) throws Exception {
        if (in == null || in.length() == 0) return null;
        return Year.parse(in, dateTimeFormatter);
    }
}
