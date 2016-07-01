package org.sfm.utils.conv.time;

import org.sfm.utils.conv.Converter;

import java.time.Month;
import java.time.MonthDay;
import java.time.Year;
import java.time.ZoneId;
import java.util.Date;

public class DateToJavaYearConverter implements Converter<Date, Year> {
    private final ZoneId zoneId;

    public DateToJavaYearConverter(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public Year convert(Date in) throws Exception {
        if (in == null) return null;
        return Year.of(in.toInstant().atZone(zoneId).getYear());
    }
}
