package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class DateToJavaLocalTimeConverter implements Converter<Date, LocalTime> {
    private final ZoneId dateTimeZone;

    public DateToJavaLocalTimeConverter(ZoneId dateTimeZone) {
        this.dateTimeZone = dateTimeZone;
    }

    @Override
    public LocalTime convert(Date in, Context context) throws Exception {
        if (in == null) return null;
        return in.toInstant().atZone(dateTimeZone).toLocalTime();
    }
}
