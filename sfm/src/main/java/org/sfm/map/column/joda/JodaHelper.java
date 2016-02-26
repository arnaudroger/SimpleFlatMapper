package org.sfm.map.column.joda;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.sfm.map.mapper.ColumnDefinition;
import org.sfm.map.column.DateFormatProperty;
import org.sfm.map.column.TimeZoneProperty;
import org.sfm.reflect.meta.ObjectClassMeta;

import java.util.TimeZone;

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

    public static DateTimeZone getDateTimeZoneOrDefault(Object[] params) {
        if (params != null) {
            for(Object p : params) {
                if (p instanceof ColumnDefinition) {
                    final DateTimeZone dateTimeZone = _dateTimeZone((ColumnDefinition<?, ?>) p);
                    if (dateTimeZone != null) return dateTimeZone;
                } else if (p instanceof DateTimeZone) {
                    return (DateTimeZone) p;
                } else if (p instanceof TimeZone) {
                    return DateTimeZone.forTimeZone((TimeZone)p);
                } else if (p instanceof JodaDateTimeZoneProperty) {
                    return ((JodaDateTimeZoneProperty)p).getZone();
                } else if (p instanceof TimeZoneProperty) {
                    return DateTimeZone.forTimeZone(((TimeZoneProperty)p).getTimeZone());
                }
            }
        }
        return DateTimeZone.getDefault();
    }
}
