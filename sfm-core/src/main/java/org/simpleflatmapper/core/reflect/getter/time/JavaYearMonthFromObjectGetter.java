package org.simpleflatmapper.core.reflect.getter.time;

import org.simpleflatmapper.core.reflect.Getter;

import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.Date;


public class JavaYearMonthFromObjectGetter<S> implements Getter<S, YearMonth> {

    private final Getter<S, ?> getter;
    private final ZoneId zone;

    public JavaYearMonthFromObjectGetter(Getter<S, ?> getter, ZoneId zoneId) {
        this.getter = getter;
        this.zone = zoneId;
    }

    @Override
    public YearMonth get(S target) throws Exception {
        Object o = getter.get(target);

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

    @Override
    public String toString() {
        return "JavaYearMonthFromObjectGetter{" +
                "getter=" + getter +
                '}';
    }
}
