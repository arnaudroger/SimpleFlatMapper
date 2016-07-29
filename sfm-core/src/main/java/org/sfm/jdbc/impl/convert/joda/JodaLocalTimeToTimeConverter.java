package org.sfm.jdbc.impl.convert.joda;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.sfm.utils.conv.Converter;

import java.sql.Time;

public class JodaLocalTimeToTimeConverter implements Converter<LocalTime, Time> {
    private final DateTimeZone dateTimeZone;

    public JodaLocalTimeToTimeConverter(DateTimeZone dateTimeZone) {
        this.dateTimeZone = dateTimeZone;
    }

    @Override
    public Time convert(LocalTime in) throws Exception {
        if (in == null) return null;
        return new Time(in.toDateTimeToday(dateTimeZone).getMillis());
    }
}
