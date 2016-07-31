package org.sfm.map.column.joda;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.sfm.map.column.DefaultDateFormatProperty;
import org.sfm.map.column.TimeZoneProperty;
import org.sfm.map.mapper.ColumnDefinition;
import org.sfm.map.column.DateFormatProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class JodaHelper {

    public static DateTimeFormatter[] getDateTimeFormatters(ColumnDefinition<?, ?> columnDefinition) {
        final DateTimeZone dateTimeZone = _dateTimeZone(columnDefinition);

        List<DateTimeFormatter> dtf = new ArrayList<>();

        for(JodaDateTimeFormatterProperty prop : columnDefinition.lookForAll(JodaDateTimeFormatterProperty.class)) {
            dtf.add(withZone(prop.getFormatter(), dateTimeZone));
        }

        for(DateFormatProperty prop : columnDefinition.lookForAll(DateFormatProperty.class)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(prop.getPattern());
            dtf.add(withZone(dateTimeFormatter, dateTimeZone));
        }
        if (dtf.isEmpty()) {
            DefaultDateFormatProperty defaultDateFormatProperty = columnDefinition.lookFor(DefaultDateFormatProperty.class);

            if (defaultDateFormatProperty == null) {
                throw new IllegalStateException("No date format specified");
            }
            dtf.add(withZone(DateTimeFormat.forPattern(defaultDateFormatProperty.getPattern()), dateTimeZone));
        }

        return dtf.toArray(new DateTimeFormatter[0]);
    }

    private static DateTimeFormatter withZone(DateTimeFormatter dateTimeFormatter, DateTimeZone zoneId) {
        if (zoneId != null) {
            return dateTimeFormatter.withZone(zoneId);
        } else if (dateTimeFormatter.getZone() == null) {
            return dateTimeFormatter.withZone(DateTimeZone.getDefault());
        }
        return dateTimeFormatter;
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
