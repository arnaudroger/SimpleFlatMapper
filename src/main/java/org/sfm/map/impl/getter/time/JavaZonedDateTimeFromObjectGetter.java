package org.sfm.map.impl.getter.time;

import org.sfm.reflect.Getter;

import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.Date;


public class JavaZonedDateTimeFromObjectGetter<S> implements Getter<S, ZonedDateTime> {
    private final Getter<S, ?> getter;
    private final ZoneId zone;

    public JavaZonedDateTimeFromObjectGetter(Getter<S, ?> getter, ZoneId zoneId) {
        this.getter = getter;
        this.zone = zoneId;
    }

    @Override
    public ZonedDateTime get(S target) throws Exception {
        Object o = getter.get(target);

        if (o == null) {
            return null;
        }

        if (o instanceof Date) {
            return Instant.ofEpochMilli(((Date) o).getTime()).atZone(zone);
        }

        if (o instanceof ZonedDateTime) {
            return (ZonedDateTime) o;
        }

        if (o instanceof LocalDateTime) {
            return ((LocalDateTime)o).atZone(zone);
        }

        if (o instanceof TemporalAccessor) {
            return ZonedDateTime.from((TemporalAccessor) o);
        }

        throw new IllegalArgumentException("Cannot convert " + o + " to ZonedDateTime");
    }

    @Override
    public String toString() {
        return "JavaZonedDateTimeFromObjectGetter{" +
                "getter=" + getter +
                '}';
    }
}
