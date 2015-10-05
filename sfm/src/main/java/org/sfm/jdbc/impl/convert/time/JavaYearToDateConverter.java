package org.sfm.jdbc.impl.convert.time;

import org.sfm.utils.conv.Converter;

import java.sql.Date;
import java.time.MonthDay;
import java.time.Year;
import java.time.ZoneId;

public class JavaYearToDateConverter implements Converter<Year, Date> {
    private final ZoneId zoneId;

    public JavaYearToDateConverter(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public Date convert(Year in) throws Exception {
        if (in == null) return null;
        return new Date(in.atMonthDay(MonthDay.now()).atStartOfDay(zoneId).toInstant().toEpochMilli());
    }
}
