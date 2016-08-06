package org.simpleflatmapper.map.column.time;

import org.simpleflatmapper.map.column.ColumnProperty;
import org.simpleflatmapper.util.date.time.DateTimeFormatterSupplier;

import java.time.format.DateTimeFormatter;

public class JavaDateTimeFormatterProperty implements ColumnProperty, DateTimeFormatterSupplier {
    private final DateTimeFormatter formatter;

    public JavaDateTimeFormatterProperty(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    public DateTimeFormatter get() {
        return formatter;
    }
}
