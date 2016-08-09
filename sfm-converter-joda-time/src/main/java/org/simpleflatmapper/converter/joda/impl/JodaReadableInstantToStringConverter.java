package org.simpleflatmapper.converter.joda.impl;

import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormatter;
import org.simpleflatmapper.converter.Converter;


public class JodaReadableInstantToStringConverter implements Converter<ReadableInstant, String> {

    private final DateTimeFormatter dateTimeFormatter;

    public JodaReadableInstantToStringConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public String convert(ReadableInstant in) throws Exception {
        return dateTimeFormatter.print(in);
    }
}
