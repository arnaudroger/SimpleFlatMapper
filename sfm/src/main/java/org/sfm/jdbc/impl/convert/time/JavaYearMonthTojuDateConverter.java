package org.sfm.jdbc.impl.convert.time;

import org.sfm.utils.conv.Converter;

import java.util.Date;
import java.time.YearMonth;
import java.time.ZoneId;

public class JavaYearMonthTojuDateConverter implements Converter<YearMonth, Date> {
    private final ZoneId zoneId;

    public JavaYearMonthTojuDateConverter(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public Date convert(YearMonth in) throws Exception {
        if (in == null) return null;
        return Date.from(in.atDay(1).atStartOfDay(zoneId).toInstant());
    }
}
