package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.Date;


public class ObjectToJavaYearMonthConverter implements ContextualConverter<Object, YearMonth> {

    private final ZoneId zone;

    public ObjectToJavaYearMonthConverter(ZoneId zoneId) {
        this.zone = zoneId;
    }

    @Override
    public YearMonth convert(Object o, Context context) throws Exception {
        if (o == null) {
            return null;
        }

        if (o instanceof Date) {
            final ZonedDateTime dateTime = Instant.ofEpochMilli(((Date) o).getTime()).atZone(zone);
            return YearMonth.of(dateTime.getYear(), dateTime.getMonth());
        }

        if (o instanceof Integer || o instanceof Long) {
            int l = ((Number)o).intValue();
            int year = l / 100;
            int month = l % 100;
            return YearMonth.of(year, month);
        }

        if (o instanceof TemporalAccessor) {
            return YearMonth.from((TemporalAccessor) o);
        }

        throw new IllegalArgumentException("Cannot convert " + o + " to YearMonth");
    }
}
