package org.sfm.map.getter.impl.time;

import org.sfm.reflect.Getter;

import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.Date;


public class JavaInstantFromObjectGetter<S> implements Getter<S, Instant> {

    private final Getter<S, ?> getter;

    public JavaInstantFromObjectGetter(Getter<S, ?> getter) {
        this.getter = getter;
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
