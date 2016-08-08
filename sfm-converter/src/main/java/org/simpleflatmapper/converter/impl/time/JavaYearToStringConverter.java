package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Converter;

import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class JavaYearToStringConverter implements Converter<Year, String> {

    private final DateTimeFormatter dateTimeFormatter;

    public JavaYearToStringConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public String convert(Year in) throws Exception {
        return dateTimeFormatter.format(in);
    }
}
