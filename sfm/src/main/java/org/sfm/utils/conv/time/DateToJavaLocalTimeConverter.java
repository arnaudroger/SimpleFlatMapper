package org.sfm.utils.conv.time;

import org.sfm.utils.conv.Converter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class DateToJavaLocalTimeConverter implements Converter<Date, LocalTime> {
    private final ZoneId dateTimeZone;

    public DateToJavaLocalTimeConverter(ZoneId dateTimeZone) {
        this.dateTimeZone = dateTimeZone;
    }

    @Override
    public LocalTime convert(Date in) throws Exception {
        if (in == null) return null;
        return in.toInstant().atZone(dateTimeZone).toLocalTime();
    }
}
