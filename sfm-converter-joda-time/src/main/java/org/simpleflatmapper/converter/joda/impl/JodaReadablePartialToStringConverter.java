package org.simpleflatmapper.converter.joda.impl;

import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormatter;
import org.simpleflatmapper.converter.Converter;

import java.io.IOException;


public class JodaReadablePartialToStringConverter implements Converter<ReadablePartial, String> {

    private final DateTimeFormatter dateTimeFormatter;

    public JodaReadablePartialToStringConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public String convert(ReadablePartial in) throws Exception {
        return dateTimeFormatter.print(in);
    }
}
