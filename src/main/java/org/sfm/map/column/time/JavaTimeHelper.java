package org.sfm.map.column.time;

import org.sfm.map.column.ColumnDefinitionImpl;
import org.sfm.map.column.DateFormatProperty;
import org.sfm.map.column.TimeZoneProperty;
import org.sfm.map.column.joda.JodaDateTimeFormatterProperty;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class JavaTimeHelper {

    public static DateTimeFormatter getDateTimeFormatter(ColumnDefinitionImpl<?, ?> columnDefinition) {
        DateTimeFormatter dtf;

        if (columnDefinition.has(JodaDateTimeFormatterProperty.class)) {
            dtf = columnDefinition.lookFor(JavaDateTimeFormatterProperty.class).getFormatter();
        } else if (columnDefinition.has(DateFormatProperty.class)) {
            dtf = DateTimeFormatter.ofPattern(columnDefinition.lookFor(DateFormatProperty.class).getPattern());
        } else {
            throw new IllegalArgumentException("No date format pattern specified");
        }

       if (columnDefinition.has(TimeZoneProperty.class))
           dtf = dtf.withZone(columnDefinition.lookFor(TimeZoneProperty.class).getTimeZone().toZoneId());
       else {
           dtf = dtf.withZone(ZoneId.systemDefault());
        }

        return dtf;
    }

}
