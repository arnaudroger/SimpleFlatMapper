package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateToJavaZonedDateTimeConverter implements ContextualConverter<Date, ZonedDateTime> {
    private final ZoneId zoneId;

    public DateToJavaZonedDateTimeConverter(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public ZonedDateTime convert(Date in, Context context) throws Exception {
        if (in == null) return null;
        return in.toInstant().atZone(zoneId);
    }
}
