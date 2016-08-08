package org.simpleflatmapper.converter.joda.impl;

import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormatter;
import org.simpleflatmapper.converter.AppenderConverter;

import java.io.IOException;


public class JodaReadableInstantToStringConverter implements AppenderConverter<ReadableInstant, String> {

    private final DateTimeFormatter dateTimeFormatter;

    public JodaReadableInstantToStringConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public String convert(ReadableInstant in) throws Exception {
        return dateTimeFormatter.print(in);
    }

    @Override
    public void appendTo(ReadableInstant in, Appendable appendable) throws IOException {
        dateTimeFormatter.printTo(appendable, in);
    }
}
