package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Converter;

import java.time.OffsetTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class JavaYearMonthToStringConverter implements Converter<YearMonth, String> {

    private final DateTimeFormatter dateTimeFormatter;

    public JavaYearMonthToStringConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public String convert(YearMonth in) throws Exception {
        return dateTimeFormatter.format(in);
    }
}
