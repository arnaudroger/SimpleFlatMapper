package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.Month;
import java.util.Date;
import java.time.MonthDay;
import java.time.Year;
import java.time.ZoneId;

public class JavaYearTojuDateConverter implements ContextualConverter<Year, Date> {
    private final ZoneId zoneId;

    public JavaYearTojuDateConverter(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public Date convert(Year in, Context context) throws Exception {
        if (in == null) return null;
        return Date.from(in.atMonthDay(MonthDay.of(Month.JANUARY, 1)).atStartOfDay(zoneId).toInstant());
    }
}
