package org.sfm.map.column.joda;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.sfm.map.column.ColumnDefinitionImpl;
import org.sfm.map.column.DateFormatProperty;
import org.sfm.map.column.TimeZoneProperty;

public class JodaHelper {

    public static DateTimeFormatter getDateTimeFormatter(ColumnDefinitionImpl<?, ?> columnDefinition) {
        DateTimeFormatter dtf;

        if (columnDefinition.has(JodaDateTimeFormatterProperty.class)) {
            dtf = columnDefinition.lookFor(JodaDateTimeFormatterProperty.class).getFormatter();
        } else if (columnDefinition.has(DateFormatProperty.class)) {
            dtf = DateTimeFormat.forPattern(columnDefinition.lookFor(DateFormatProperty.class).getPattern());
        } else {
            throw new IllegalArgumentException("No date format pattern specified");
        }

        if (columnDefinition.has(JodaDateTimeZoneProperty.class)) {
            dtf = dtf.withZone(columnDefinition.lookFor(JodaDateTimeZoneProperty.class).getZone());
        } else if (columnDefinition.has(TimeZoneProperty.class)) {
            dtf = dtf.withZone(DateTimeZone.forTimeZone(columnDefinition.lookFor(TimeZoneProperty.class).getTimeZone()));
        } else {
            dtf = dtf.withZone(DateTimeZone.getDefault());
        }

        return dtf;
    }

}
