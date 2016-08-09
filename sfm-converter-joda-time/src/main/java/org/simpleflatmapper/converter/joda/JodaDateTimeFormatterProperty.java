package org.simpleflatmapper.converter.joda;


import org.joda.time.format.DateTimeFormatter;
import org.simpleflatmapper.util.Supplier;

public class JodaDateTimeFormatterProperty implements Supplier<DateTimeFormatter> {
    private final DateTimeFormatter formatter;

    public JodaDateTimeFormatterProperty(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    public DateTimeFormatter get() {
        return formatter;
    }

}
