package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.Date;


public class ObjectToJavaOffsetTimeConverter implements ContextualConverter<Object, OffsetTime> {
    private final ZoneId zone;

    public ObjectToJavaOffsetTimeConverter(ZoneId zoneId) {
        this.zone = zoneId;
    }

    @Override
    public OffsetTime convert(Object o, Context context) throws Exception {
        if (o == null) {
            return null;
        }

        if (o instanceof Date) {
            final Instant instant = Instant.ofEpochMilli(((Date) o).getTime());
            return instant.atOffset(zone.getRules().getOffset(instant)).toOffsetTime();
        }

        if (o instanceof Instant) {
            final Instant instant = (Instant) o;
            return instant.atOffset(zone.getRules().getOffset(instant)).toOffsetTime();
        }

        if (o instanceof OffsetTime) {
            return (OffsetTime) o;
        }

        if (o instanceof LocalDateTime) {
            return ((LocalDateTime)o).atZone(zone).toOffsetDateTime().toOffsetTime();
        }

        if (o instanceof LocalTime) {
            return ((LocalTime)o).atOffset(zone.getRules().getOffset(Instant.now()));
        }

        if (o instanceof TemporalAccessor) {
            return OffsetTime.from((TemporalAccessor)o);
        }

        throw new IllegalArgumentException("Cannot convert " + o + " to OffsetTime");
    }

}
