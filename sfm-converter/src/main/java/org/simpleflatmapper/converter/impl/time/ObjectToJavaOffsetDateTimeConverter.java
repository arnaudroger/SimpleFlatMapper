package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.*;
import java.util.Date;


public class ObjectToJavaOffsetDateTimeConverter implements ContextualConverter<Object, OffsetDateTime> {
    private final ZoneId zone;

    public ObjectToJavaOffsetDateTimeConverter(ZoneId zoneId) {
        this.zone = zoneId;
    }

    @Override
    public OffsetDateTime convert(Object o, Context context) throws Exception {
        if (o == null) {
            return null;
        }

        if (o instanceof Date) {
            final Instant instant = Instant.ofEpochMilli(((Date) o).getTime());
            return instant.atOffset(zone.getRules().getOffset(instant));
        }

        if (o instanceof Instant) {
            final Instant instant = (Instant) o;
            return instant.atOffset(zone.getRules().getOffset(instant));
        }

        if (o instanceof OffsetDateTime) {
            return (OffsetDateTime) o;
        }

        if (o instanceof ZonedDateTime) {
            return ((ZonedDateTime)o).toOffsetDateTime();
        }

        if (o instanceof LocalDateTime) {
            final LocalDateTime localDateTime = (LocalDateTime) o;
            return localDateTime.atOffset(zone.getRules().getOffset(localDateTime));
        }

        if (o instanceof LocalDate) {
            LocalDateTime localDateTime = ((LocalDate) o).atTime(0, 0);
            return localDateTime.atOffset(zone.getRules().getOffset(localDateTime));
        }

        throw new IllegalArgumentException("Cannot convert " + o + " to OffsetDateTime");
    }
}
