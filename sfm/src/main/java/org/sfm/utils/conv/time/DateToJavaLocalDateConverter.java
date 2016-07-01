package org.sfm.utils.conv.time;

import org.sfm.utils.conv.Converter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateToJavaLocalDateConverter implements Converter<Date, LocalDate> {
    private final ZoneId dateTimeZone;

    public DateToJavaLocalDateConverter(ZoneId dateTimeZone) {
        this.dateTimeZone = dateTimeZone;
    }

    @Override
    public LocalDate convert(Date in) throws Exception {
        if (in == null) return null;
        return in.toInstant().atZone(dateTimeZone).toLocalDate();
    }
}
