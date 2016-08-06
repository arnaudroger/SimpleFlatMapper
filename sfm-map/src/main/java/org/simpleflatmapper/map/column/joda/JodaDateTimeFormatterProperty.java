package org.simpleflatmapper.map.column.joda;


import org.joda.time.format.DateTimeFormatter;
import org.simpleflatmapper.map.column.ColumnProperty;
import org.simpleflatmapper.util.date.joda.DateTimeFormatterSupplier;

public class JodaDateTimeFormatterProperty implements ColumnProperty, DateTimeFormatterSupplier {
    private final DateTimeFormatter formatter;

    public JodaDateTimeFormatterProperty(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    public DateTimeFormatter get() {
        return formatter;
    }

    @Override
    public String toString() {
        return "JodaDateTimeFormatterProperty{" +
                formatter +
                '}';
    }
}
