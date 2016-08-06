package org.simpleflatmapper.reflect.getter.time;

import org.simpleflatmapper.reflect.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Date;


public class JavaInstantFromObjectGetter<S> implements Getter<S, Instant> {

    private final Getter<S, ?> getter;
    private final ZoneId zoneId;

    public JavaInstantFromObjectGetter(Getter<S, ?> getter, ZoneId zoneId) {
        this.getter = getter;
        this.zoneId = zoneId;
    }

    @Override
    public Instant get(S target) throws Exception {
        Object o = getter.get(target);

        if (o == null) {
            return null;
        }

        if (o instanceof Date) {
            return Instant.ofEpochMilli(((Date) o).getTime());
        }

        if (o instanceof LocalDateTime) {
            return ((LocalDateTime)o).atZone(zoneId).toInstant();
        }

        if (o instanceof TemporalAccessor) {
            return Instant.from((TemporalAccessor) o);
        }

        if (o instanceof Long || o instanceof Integer) {
            return Instant.ofEpochMilli(((Number)o).longValue());
        }

        throw new IllegalArgumentException("Cannot convert " + o + " to Instant");
    }

    @Override
    public String toString() {
        return "JavaInstantFromObjectGetter{" +
                "getter=" + getter +
                '}';
    }
}
