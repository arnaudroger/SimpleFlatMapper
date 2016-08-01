package org.sfm.utils.conv.joda;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.sfm.utils.conv.Converter;

import java.util.Date;

public class JodaLocalDateTimeTojuDateConverter implements Converter<LocalDateTime, Date> {
    private final DateTimeZone dateTimeZone;

    public JodaLocalDateTimeTojuDateConverter(DateTimeZone dateTimeZone) {
        this.dateTimeZone = dateTimeZone;
    }

    @Override
    public Date convert(LocalDateTime in) throws Exception {
        if (in == null) return null;
        return in.toDate(dateTimeZone.toTimeZone());
    }
}
