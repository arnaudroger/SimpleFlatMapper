package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateToJavaYearMonthConverter implements ContextualConverter<Date, YearMonth> {
    private final ZoneId zoneId;

    public DateToJavaYearMonthConverter(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public YearMonth convert(Date in, Context context) throws Exception {
        if (in == null) return null;
        ZonedDateTime zonedDateTime = in.toInstant().atZone(zoneId);
        return YearMonth.of(zonedDateTime.getYear(), zonedDateTime.getMonth());
    }
}
