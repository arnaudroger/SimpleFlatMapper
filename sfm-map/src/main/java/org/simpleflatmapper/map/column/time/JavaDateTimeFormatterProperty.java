package org.simpleflatmapper.map.column.time;

import org.simpleflatmapper.converter.impl.time.DateTimeFormatterSupplier;
import org.simpleflatmapper.map.column.ColumnProperty;

import java.time.format.DateTimeFormatter;

public class JavaDateTimeFormatterProperty implements ColumnProperty, DateTimeFormatterSupplier {
    private final DateTimeFormatter formatter;

    public JavaDateTimeFormatterProperty(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public DateTimeFormatter get() {
        return formatter;
    }
}
