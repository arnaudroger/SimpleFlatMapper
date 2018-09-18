package org.simpleflatmapper.converter.impl.time;


import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

public class JavaTemporalToStringConverter implements ContextualConverter<Temporal, String> {

    private final DateTimeFormatter dateTimeFormatter;

    public JavaTemporalToStringConverter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public String convert(Temporal in, Context context) throws Exception {
        return dateTimeFormatter.format(in);
    }
}
