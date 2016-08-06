package org.simpleflatmapper.reflect.getter.time;

import org.simpleflatmapper.reflect.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Date;


public class JavaLocalDateTimeFromObjectGetter<S> implements Getter<S, LocalDateTime> {
    private final Getter<S, ?> getter;
    private final ZoneId zone;

    public JavaLocalDateTimeFromObjectGetter(Getter<S, ?> getter, ZoneId zoneId) {
        this.getter = getter;
        this.zone = zoneId;
    }

    @Override
    public LocalDateTime get(S target) throws Exception {
        Object o = getter.get(target);

        if (o == null) {
            return null;
        }

        if (o instanceof Date) {
            return Instant.ofEpochMilli(((Date) o).getTime()).atZone(zone).toLocalDateTime();
        }

        if (o instanceof Instant) {
            final Instant instant = (Instant) o;
            return instant.atZone(zone).toLocalDateTime();
        }

        if (o instanceof LocalDateTime) {
            return (LocalDateTime) o;
        }

        if (o instanceof TemporalAccessor) {
            return LocalDateTime.from((TemporalAccessor) o);
        }

        throw new IllegalArgumentException("Cannot convert " + o + " to LocalDateTime");
    }

    @Override
    public String toString() {
        return "JavaLocalDateTimeFromObjectGetter{" +
                "getter=" + getter +
                '}';
    }
}
