package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;

import java.time.OffsetTime;
import java.time.ZoneId;
import java.util.Date;

public class DateToJavaOffsetTimeConverter implements Converter<Date, OffsetTime> {
    private final ZoneId dateTimeZone;

    public DateToJavaOffsetTimeConverter(ZoneId dateTimeZone) {
        this.dateTimeZone = dateTimeZone;
    }

    @Override
    public OffsetTime convert(Date in, Context context) throws Exception {
        if (in == null) return null;
        return in.toInstant().atZone(dateTimeZone).toOffsetDateTime().toOffsetTime();
    }
}
