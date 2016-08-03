package org.simpleflatmapper.core.reflect.getter.time;


import org.simpleflatmapper.core.reflect.Getter;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Date;


public class JavaLocalTimeFromObjectGetter<S> implements Getter<S, LocalTime> {
    private final Getter<S, ?> getter;
    private final ZoneId zone;

    public JavaLocalTimeFromObjectGetter(Getter<S, ?> getter, ZoneId zoneId) {
        this.getter = getter;
        this.zone = zoneId;
    }

    @Override
    public LocalTime get(S target) throws Exception {
        Object o = getter.get(target);

        if (o == null) {
            return null;
        }

        if (o instanceof Date) {
            return Instant.ofEpochMilli(((Date) o).getTime()).atZone(zone).toLocalTime();
        }

        if (o instanceof Instant) {
            return ((Instant)o).atZone(zone).toLocalTime();
        }

        if (o instanceof LocalTime) {
            return (LocalTime) o;
        }

        if (o instanceof TemporalAccessor) {
            return LocalTime.from((TemporalAccessor) o);
        }

        throw new IllegalArgumentException("Cannot convert " + o + " to LocalTime");
    }

    @Override
    public String toString() {
        return "JavaLocalTimeFromObjectGetter{" +
                "getter=" + getter +
                '}';
    }
}
