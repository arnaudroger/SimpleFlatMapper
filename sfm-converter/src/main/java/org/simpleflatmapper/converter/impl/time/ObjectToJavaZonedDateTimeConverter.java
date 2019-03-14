package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.Date;


public class ObjectToJavaZonedDateTimeConverter implements ContextualConverter<Object, ZonedDateTime> {
    private final ZoneId zone;

    public ObjectToJavaZonedDateTimeConverter(ZoneId zoneId) {
        this.zone = zoneId;
    }

    @Override
    public ZonedDateTime convert(Object o, Context context) throws Exception {
        if (o == null) {
            return null;
        }

        if (o instanceof Date) {
            return Instant.ofEpochMilli(((Date) o).getTime()).atZone(zone);
        }

        if (o instanceof Instant) {
            return((Instant)o).atZone(zone);
        }

        if (o instanceof ZonedDateTime) {
            return (ZonedDateTime) o;
        }

        if (o instanceof LocalDateTime) {
            return ((LocalDateTime)o).atZone(zone);
        }

        if (o instanceof TemporalAccessor) {
            return ZonedDateTime.from((TemporalAccessor) o).withZoneSameLocal(zone);
        }

        throw new IllegalArgumentException("Cannot convert " + o + " to ZonedDateTime");
    }
}
