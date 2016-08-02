package org.simpleflatmapper.core.reflect.getter.impl.time;

import org.simpleflatmapper.core.reflect.Getter;

import java.time.*;
import java.util.Date;


public class JavaOffsetDateTimeFromObjectGetter<S> implements Getter<S, OffsetDateTime> {
    private final Getter<S, ?> getter;
    private final ZoneId zone;

    public JavaOffsetDateTimeFromObjectGetter(Getter<S, ?> getter, ZoneId zoneId) {
        this.getter = getter;
        this.zone = zoneId;
    }

    @Override
    public OffsetDateTime get(S target) throws Exception {
        Object o = getter.get(target);

        if (o == null) {
            return null;
        }

        if (o instanceof Date) {
            final Instant instant = Instant.ofEpochMilli(((Date) o).getTime());
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

    @Override
    public String toString() {
        return "JavaOffsetDateTimeFromObjectGetter{" +
                "getter=" + getter +
                '}';
    }
}
