package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Converter;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateToJavaOffsetDateTimeConverter implements Converter<Date, OffsetDateTime> {
    private final ZoneId dateTimeZone;

    public DateToJavaOffsetDateTimeConverter(ZoneId dateTimeZone) {
        this.dateTimeZone = dateTimeZone;
    }

    @Override
    public OffsetDateTime convert(Date in) throws Exception {
        if (in == null) return null;
        return in.toInstant().atZone(dateTimeZone).toOffsetDateTime();
    }
}
