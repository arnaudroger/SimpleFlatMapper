package org.sfm.map.column.time;

import org.sfm.map.ColumnDefinition;
import org.sfm.map.column.DateFormatProperty;
import org.sfm.map.column.TimeZoneProperty;
import org.sfm.map.column.joda.JodaDateTimeFormatterProperty;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class JavaTimeHelper {

    public static DateTimeFormatter getDateTimeFormatter(ColumnDefinition<?, ?> columnDefinition) {
        DateTimeFormatter dtf;

        if (columnDefinition.has(JodaDateTimeFormatterProperty.class)) {
            dtf = columnDefinition.lookFor(JavaDateTimeFormatterProperty.class).getFormatter();
        } else if (columnDefinition.has(DateFormatProperty.class)) {
            dtf = DateTimeFormatter.ofPattern(columnDefinition.lookFor(DateFormatProperty.class).getPattern());
        } else {
            throw new IllegalArgumentException("No date format pattern specified");
        }

        dtf = dtf.withZone(getZoneId(columnDefinition));

        return dtf;
    }

    public static ZoneId getZoneId(ColumnDefinition<?, ?> columnDefinition) {
        if (columnDefinition.has(JavaZoneIdProperty.class)) {
            return columnDefinition.lookFor(JavaZoneIdProperty.class).getZoneId();
        } else if (columnDefinition.has(TimeZoneProperty.class)) {
            return columnDefinition.lookFor(TimeZoneProperty.class).getTimeZone().toZoneId();
        }

        return ZoneId.systemDefault();
    }

}
