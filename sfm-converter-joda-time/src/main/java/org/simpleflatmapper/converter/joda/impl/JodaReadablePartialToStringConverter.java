package org.simpleflatmapper.converter.joda.impl;

import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormatter;
import org.simpleflatmapper.converter.AppenderConverter;

import java.io.IOException;


public class JodaReadablePartialToStringConverter implements AppenderConverter<ReadablePartial, String> {

    private final DateTimeFormatter dateTimeFormatter;

    public JodaReadablePartialToStringConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public String convert(ReadablePartial in) throws Exception {
        return dateTimeFormatter.print(in);
    }

    @Override
    public void appendTo(ReadablePartial in, Appendable appendable) throws IOException {
        dateTimeFormatter.printTo(appendable, in);
    }
}
