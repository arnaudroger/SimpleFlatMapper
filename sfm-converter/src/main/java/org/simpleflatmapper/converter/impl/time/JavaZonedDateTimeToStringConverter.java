package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Converter;

import java.time.Year;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class JavaZonedDateTimeToStringConverter implements Converter<ZonedDateTime, String> {

    private final DateTimeFormatter dateTimeFormatter;

    public JavaZonedDateTimeToStringConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public String convert(ZonedDateTime in) throws Exception {
        return dateTimeFormatter.format(in);
    }
}
