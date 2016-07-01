package org.sfm.utils.conv.time;

import org.sfm.utils.conv.Converter;

import java.time.LocalDate;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.util.Date;

public class DateToJavaOffsetTimeConverter implements Converter<Date, OffsetTime> {
    private final ZoneId dateTimeZone;

    public DateToJavaOffsetTimeConverter(ZoneId dateTimeZone) {
        this.dateTimeZone = dateTimeZone;
    }

    @Override
    public OffsetTime convert(Date in) throws Exception {
        if (in == null) return null;
        return in.toInstant().atZone(dateTimeZone).toOffsetDateTime().toOffsetTime();
    }
}
