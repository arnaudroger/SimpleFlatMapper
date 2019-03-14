package org.simpleflatmapper.converter.impl.time;


import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Date;


public class ObjectToJavaInstantConverter implements ContextualConverter<Object, Instant> {

    private final ZoneId zoneId;

    public ObjectToJavaInstantConverter(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public Instant convert(Object in, Context context) throws Exception {
        if (in == null) {
            return null;
        }

        if (in instanceof Date) {
            return Instant.ofEpochMilli(((Date) in).getTime());
        }

        if (in instanceof LocalDateTime) {
            return ((LocalDateTime)in).atZone(zoneId).toInstant();
        }

        if (in instanceof TemporalAccessor) {
            return Instant.from((TemporalAccessor) in);
        }

        if (in instanceof Long || in instanceof Integer) {
            return Instant.ofEpochMilli(((Number)in).longValue());
        }

        throw new IllegalArgumentException("Cannot convert " + in + " to Instant");
    }
}
