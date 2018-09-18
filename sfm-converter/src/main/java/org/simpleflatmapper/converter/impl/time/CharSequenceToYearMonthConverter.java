package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class CharSequenceToYearMonthConverter implements ContextualConverter<CharSequence, YearMonth> {

    private final DateTimeFormatter dateTimeFormatter;

    public CharSequenceToYearMonthConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public YearMonth convert(CharSequence in, Context context) throws Exception {
        if (in == null || in.length() == 0) return null;
        return YearMonth.parse(in, dateTimeFormatter);
    }
}
