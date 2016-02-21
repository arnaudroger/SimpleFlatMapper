package org.sfm.map.column.joda;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.sfm.map.mapper.ColumnDefinition;
import org.sfm.map.column.DateFormatProperty;
import org.sfm.map.column.TimeZoneProperty;

public class JodaHelper {

    public static DateTimeFormatter getDateTimeFormatter(ColumnDefinition<?, ?> columnDefinition) {
        DateTimeFormatter dtf;

        if (columnDefinition.has(JodaDateTimeFormatterProperty.class)) {
            dtf = columnDefinition.lookFor(JodaDateTimeFormatterProperty.class).getFormatter();
        } else if (columnDefinition.has(DateFormatProperty.class)) {
            dtf = DateTimeFormat.forPattern(columnDefinition.lookFor(DateFormatProperty.class).getPattern());
        } else {
            throw new IllegalArgumentException("No date format pattern specified");
        }


        final DateTimeZone dateTimeZone = _dateTimeZone(columnDefinition);

        if (dateTimeZone != null) {
            dtf = dtf.withZone(dateTimeZone);
        } else if (dtf.getZone() == null) {
            dtf = dtf.withZone(DateTimeZone.getDefault());
        }

        return dtf;
    }

    private static DateTimeZone _dateTimeZone(ColumnDefinition<?, ?> columnDefinition) {
        if (columnDefinition.has(JodaDateTimeZoneProperty.class)) {
            return columnDefinition.lookFor(JodaDateTimeZoneProperty.class).getZone();
        } else if (columnDefinition.has(TimeZoneProperty.class)) {
            return DateTimeZone.forTimeZone(columnDefinition.lookFor(TimeZoneProperty.class).getTimeZone());
        }

        return null;
    }

    public static DateTimeZone getDateTimeZoneOrDefault(ColumnDefinition<?, ?> columnDefinition) {
        final DateTimeZone dateTimeZone = _dateTimeZone(columnDefinition);

        return dateTimeZone == null ? DateTimeZone.getDefault() : dateTimeZone;
    }

}
