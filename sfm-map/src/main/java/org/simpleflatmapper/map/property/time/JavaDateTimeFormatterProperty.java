package org.simpleflatmapper.map.property.time;

import org.simpleflatmapper.util.Supplier;

import java.time.format.DateTimeFormatter;

public class JavaDateTimeFormatterProperty implements Supplier<DateTimeFormatter> {
    private final DateTimeFormatter formatter;

    public JavaDateTimeFormatterProperty(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public DateTimeFormatter get() {
        return formatter;
    }

    @Override
    public String toString() {
        return "DateTimeFormatter{" + formatter + '}';
    }
}
