package org.sfm.map.column.time;

import org.sfm.map.column.ColumnProperty;

import java.time.format.DateTimeFormatter;

public class JavaDateTimeFormatterProperty implements ColumnProperty {
    private final DateTimeFormatter formatter;

    public JavaDateTimeFormatterProperty(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    public DateTimeFormatter getFormatter() {
        return formatter;
    }
}
