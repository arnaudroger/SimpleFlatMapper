package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.AppenderConverter;

import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

public class JavaTemporalToStringConverter implements AppenderConverter<Temporal, String> {

    private final DateTimeFormatter dateTimeFormatter;

    public JavaTemporalToStringConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public String convert(Temporal in) throws Exception {
        return dateTimeFormatter.format(in);
    }

    @Override
    public void appendTo(Temporal in, Appendable appendable) {
        dateTimeFormatter.formatTo(in, appendable);
    }
}
