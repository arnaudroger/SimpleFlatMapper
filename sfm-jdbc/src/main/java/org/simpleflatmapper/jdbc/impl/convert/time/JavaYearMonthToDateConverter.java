package org.simpleflatmapper.jdbc.impl.convert.time;

import org.simpleflatmapper.converter.Converter;

import java.sql.Date;
import java.time.YearMonth;
import java.time.ZoneId;

public class JavaYearMonthToDateConverter implements Converter<YearMonth, Date> {
    private final ZoneId zoneId;

    public JavaYearMonthToDateConverter(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public Date convert(YearMonth in) throws Exception {
        if (in == null) return null;
        return new Date(in.atDay(1).atStartOfDay(zoneId).toInstant().toEpochMilli());
    }
}
