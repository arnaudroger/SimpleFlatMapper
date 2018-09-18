package org.simpleflatmapper.converter.impl.time;


import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Date;


public class ObjectToJavaLocalTimeConverter implements ContextualConverter<Object, LocalTime> {
    private final ZoneId zone;

    public ObjectToJavaLocalTimeConverter(ZoneId zoneId) {
        this.zone = zoneId;
    }

    @Override
    public LocalTime convert(Object o, Context context) throws Exception {
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
}
