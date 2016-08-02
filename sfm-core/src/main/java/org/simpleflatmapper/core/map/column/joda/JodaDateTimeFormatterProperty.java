package org.simpleflatmapper.core.map.column.joda;


import org.joda.time.format.DateTimeFormatter;
import org.simpleflatmapper.core.map.column.ColumnProperty;

public class JodaDateTimeFormatterProperty implements ColumnProperty {
    private final DateTimeFormatter formatter;

    public JodaDateTimeFormatterProperty(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }

    @Override
    public String toString() {
        return "JodaDateTimeFormatterProperty{" +
                formatter +
                '}';
    }
}
