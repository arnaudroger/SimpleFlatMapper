package org.sfm.map.column.time;

import org.sfm.map.ColumnDefinition;
import org.sfm.map.column.DateFormatProperty;
import org.sfm.map.column.TimeZoneProperty;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class JavaTimeHelper {

    public static DateTimeFormatter getDateTimeFormatter(ColumnDefinition<?, ?> columnDefinition) {
        DateTimeFormatter dtf;

        if (columnDefinition.has(JavaDateTimeFormatterProperty.class)) {
            dtf = columnDefinition.lookFor(JavaDateTimeFormatterProperty.class).getFormatter();
        } else if (columnDefinition.has(DateFormatProperty.class)) {
            dtf = DateTimeFormatter.ofPattern(columnDefinition.lookFor(DateFormatProperty.class).getPattern());
        } else {
            throw new IllegalArgumentException("No date format pattern specified");
        }

        final ZoneId zoneId = _getZoneId(columnDefinition);

        if (zoneId != null) {
            dtf = dtf.withZone(zoneId);
        } else if (dtf.getZone() == null) {
            dtf = dtf.withZone(ZoneId.systemDefault());
        }

        return dtf;
    }

    public static ZoneId getZoneIdOrDefault(ColumnDefinition<?, ?> columnDefinition) {
        ZoneId zoneId = _getZoneId(columnDefinition);
        if (zoneId != null) {
            return zoneId;
        } else {
            return ZoneId.systemDefault();
        }
    }

    private static ZoneId _getZoneId(ColumnDefinition<?, ?> columnDefinition) {
        if (columnDefinition.has(JavaZoneIdProperty.class)) {
            return columnDefinition.lookFor(JavaZoneIdProperty.class).getZoneId();
        } else if (columnDefinition.has(TimeZoneProperty.class)) {
            return columnDefinition.lookFor(TimeZoneProperty.class).getTimeZone().toZoneId();
        }

        return null;
    }

}
