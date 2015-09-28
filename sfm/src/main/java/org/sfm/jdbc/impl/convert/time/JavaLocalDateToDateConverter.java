package org.sfm.jdbc.impl.convert.time;

import org.sfm.utils.conv.Converter;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;

public class JavaLocalDateToDateConverter implements Converter<LocalDate, Date> {
    private final ZoneId dateTimeZone;

    public JavaLocalDateToDateConverter(ZoneId dateTimeZone) {
        this.dateTimeZone = dateTimeZone;
    }

    @Override
    public Date convert(LocalDate in) throws Exception {
        if (in == null) return null;
        return new Date(in.atStartOfDay(dateTimeZone).toInstant().toEpochMilli());
    }
}
