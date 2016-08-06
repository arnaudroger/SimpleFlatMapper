package org.simpleflatmapper.reflect.getter.time;

import org.simpleflatmapper.reflect.Getter;

import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.Date;


public class JavaYearFromObjectGetter<S> implements Getter<S, Year> {
    private final Getter<S, ?> getter;
    private final ZoneId zone;

    public JavaYearFromObjectGetter(Getter<S, ?> getter, ZoneId zoneId) {
        this.getter = getter;
        this.zone = zoneId;
    }

    @Override
    public Year get(S target) throws Exception {
        Object o = getter.get(target);

        if (o == null) {
            return null;
        }

        if (o instanceof Date) {
            final ZonedDateTime dateTime = Instant.ofEpochMilli(((Date) o).getTime()).atZone(zone);
            return Year.of(dateTime.getYear());
        }

        if (o instanceof Integer || o instanceof Long) {
            return Year.of(((Number)o).intValue());
        }

        if (o instanceof TemporalAccessor) {
            return Year.from((TemporalAccessor) o);
        }

        throw new IllegalArgumentException("Cannot convert " + o + " to Year");
    }

    @Override
    public String toString() {
        return "JavaYearFromObjectGetter{" +
                "getter=" + getter +
                '}';
    }
}
