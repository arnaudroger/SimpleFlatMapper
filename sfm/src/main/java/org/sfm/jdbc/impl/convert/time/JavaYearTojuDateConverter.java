package org.sfm.jdbc.impl.convert.time;

import org.sfm.utils.conv.Converter;

import java.util.Date;
import java.time.MonthDay;
import java.time.Year;
import java.time.ZoneId;

public class JavaYearTojuDateConverter implements Converter<Year, Date> {
    private final ZoneId zoneId;

    public JavaYearTojuDateConverter(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public Date convert(Year in) throws Exception {
        if (in == null) return null;
        return Date.from(in.atMonthDay(MonthDay.now()).atStartOfDay(zoneId).toInstant());
    }
}
