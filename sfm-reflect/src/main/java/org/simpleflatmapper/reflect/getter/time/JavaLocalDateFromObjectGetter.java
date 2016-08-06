package org.simpleflatmapper.reflect.getter.time;

import org.simpleflatmapper.reflect.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Date;


public class JavaLocalDateFromObjectGetter<S> implements Getter<S, LocalDate> {
    private final Getter<S, ?> getter;
    private final ZoneId zone;

    public JavaLocalDateFromObjectGetter(Getter<S, ?> getter, ZoneId zoneId) {
        this.getter = getter;
        this.zone = zoneId;
    }

    @Override
    public LocalDate get(S target) throws Exception {
        Object o = getter.get(target);

        if (o == null) {
            return null;
        }

        if (o instanceof Date) {
            return Instant.ofEpochMilli(((Date) o).getTime()).atZone(zone).toLocalDate();
        }

        if (o instanceof Instant) {
            Instant instant = (Instant) o;
            return instant.atZone(zone).toLocalDate();
        }

        if (o instanceof LocalDate) {
            return (LocalDate) o;
        }

        if (o instanceof LocalDateTime) {
            return ((LocalDateTime)o).toLocalDate();
        }

        if (o instanceof TemporalAccessor) {
            return LocalDate.from((TemporalAccessor) o);
        }

        throw new IllegalArgumentException("Cannot convert " + o + " to LocalDate");
    }

    @Override
    public String toString() {
        return "JavaLocalDateFromObjectGetter{" +
                "getter=" + getter +
                '}';
    }
}
