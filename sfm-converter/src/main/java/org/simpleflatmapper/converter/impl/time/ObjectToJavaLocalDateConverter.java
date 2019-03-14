package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

import static org.simpleflatmapper.converter.impl.time.ObjectToJavaZonedDateTimeConverter.getWithCustomAccessor;


public class ObjectToJavaLocalDateConverter implements ContextualConverter<Object, LocalDate> {
    private final ZoneId zone;

    public ObjectToJavaLocalDateConverter(ZoneId zoneId) {
        this.zone = zoneId;
    }

    @Override
    public LocalDate convert(Object o, Context context) throws Exception {
        if (o == null) {
            return null;
        }

        if (o instanceof Date) {
            return Instant.ofEpochMilli(((Date) o).getTime()).atZone(zone).toLocalDate();
        }

        if (o instanceof Instant) {
            Instant instant = (Instant) o;
            return instant.atZone(zone).toLocalDate();
        }

        if (o instanceof LocalDate) {
            return (LocalDate) o;
        }

        if (o instanceof LocalDateTime) {
            return ((LocalDateTime)o).toLocalDate();
        }

        if (o instanceof TemporalAccessor) {
            return LocalDate.from((TemporalAccessor) o);
        }

        Instant i = getWithCustomAccessor(o);
        if (i != null) {
            return i.atZone(zone).toLocalDate();
        }

        throw new IllegalArgumentException("Cannot convert " + o + " to LocalDate");
    }
}
