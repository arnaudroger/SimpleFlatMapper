package org.sfm.jdbc.impl.convert.joda;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.sfm.utils.conv.Converter;

import java.util.Date;

public class JodaLocalTimeTojuDateConverter implements Converter<LocalTime, Date> {
    private final DateTimeZone dateTimeZone;

    public JodaLocalTimeTojuDateConverter(DateTimeZone dateTimeZone) {
        this.dateTimeZone = dateTimeZone;
    }

    @Override
    public Date convert(LocalTime in) throws Exception {
        if (in == null) return null;
        return in.toDateTimeToday(dateTimeZone).toDate();
    }
}
