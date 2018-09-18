package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.Date;


public class ObjectToJavaYearConverter implements ContextualConverter<Object, Year> {
    private final ZoneId zone;

    public ObjectToJavaYearConverter(ZoneId zoneId) {
        this.zone = zoneId;
    }

    @Override
    public Year convert(Object o, Context context) throws Exception {
        if (o == null) {
            return null;
        }

        if (o instanceof Date) {
            final ZonedDateTime dateTime = Instant.ofEpochMilli(((Date) o).getTime()).atZone(zone);
            return Year.of(dateTime.getYear());
        }

        if (o instanceof Integer || o instanceof Long) {
            return Year.of(((Number)o).intValue());
        }

        if (o instanceof TemporalAccessor) {
            return Year.from((TemporalAccessor) o);
        }

        throw new IllegalArgumentException("Cannot convert " + o + " to Year");
    }
}
