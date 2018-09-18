package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.Year;
import java.time.ZoneId;
import java.util.Date;

public class DateToJavaYearConverter implements ContextualConverter<Date, Year> {
    private final ZoneId zoneId;

    public DateToJavaYearConverter(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public Year convert(Date in, Context context) throws Exception {
        if (in == null) return null;
        return Year.of(in.toInstant().atZone(zoneId).getYear());
    }
}
