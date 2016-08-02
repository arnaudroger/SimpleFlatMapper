package org.simpleflatmapper.core.map.column.time;

import org.simpleflatmapper.core.map.column.DefaultDateFormatProperty;
import org.simpleflatmapper.core.map.mapper.ColumnDefinition;
import org.simpleflatmapper.core.map.column.DateFormatProperty;
import org.simpleflatmapper.core.map.column.TimeZoneProperty;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class JavaTimeHelper {

    public static DateTimeFormatter[] getDateTimeFormatters(ColumnDefinition<?, ?> columnDefinition) {
        List<DateTimeFormatter> dtf = new ArrayList<DateTimeFormatter>();


        ZoneId zoneId = _getZoneId(columnDefinition);

        for(JavaDateTimeFormatterProperty prop : columnDefinition.lookForAll(JavaDateTimeFormatterProperty.class)) {
            dtf.add(withZone(prop.getFormatter(), zoneId));
        }

        for(DateFormatProperty prop : columnDefinition.lookForAll(DateFormatProperty.class)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(prop.getPattern());
            dtf.add(withZone(dateTimeFormatter, zoneId));
        }

        if (dtf.isEmpty()) {
            DefaultDateFormatProperty defaultDateFormatProperty = columnDefinition.lookFor(DefaultDateFormatProperty.class);

            if (defaultDateFormatProperty == null) {
                throw new IllegalStateException("No date format specified");
            }
            dtf.add(withZone(DateTimeFormatter.ofPattern(defaultDateFormatProperty.getPattern()), zoneId));
        }

        return dtf.toArray(new DateTimeFormatter[0]);
    }

    private static DateTimeFormatter withZone(DateTimeFormatter dateTimeFormatter, ZoneId zoneId) {
        if (zoneId != null) {
            return dateTimeFormatter.withZone(zoneId);
        } else if (dateTimeFormatter.getZone() == null) {
            return dateTimeFormatter.withZone(ZoneId.systemDefault());
        }
        return dateTimeFormatter;
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


    public static ZoneId getZoneIdOrDefault(Object[] params) {
        if (params != null) {
            for(Object p : params) {
                if (p instanceof ColumnDefinition) {
                    final ZoneId dateTimeZone = _getZoneId((ColumnDefinition<?, ?>) p);
                    if (dateTimeZone != null) return dateTimeZone;
                } else if (p instanceof ZoneId) {
                    return (ZoneId) p;
                } else if (p instanceof TimeZone) {
                    return ((TimeZone)p).toZoneId();
                } else if (p instanceof JavaZoneIdProperty) {
                    return ((JavaZoneIdProperty)p).getZoneId();
                } else if (p instanceof TimeZoneProperty) {
                    return (((TimeZoneProperty)p).getTimeZone()).toZoneId();
                }
            }
        }
        return ZoneId.systemDefault();
    }

}
