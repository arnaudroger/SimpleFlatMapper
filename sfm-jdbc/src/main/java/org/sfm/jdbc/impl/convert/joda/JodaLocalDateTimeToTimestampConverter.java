package org.sfm.jdbc.impl.convert.joda;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.sfm.utils.conv.Converter;

import java.sql.Timestamp;

public class JodaLocalDateTimeToTimestampConverter implements Converter<LocalDateTime, Timestamp> {
    private final DateTimeZone dateTimeZone;

    public JodaLocalDateTimeToTimestampConverter(DateTimeZone dateTimeZone) {
        this.dateTimeZone = dateTimeZone;
    }

    @Override
    public Timestamp convert(LocalDateTime in) throws Exception {
        if (in == null) return null;
        return new Timestamp(in.toDateTime(dateTimeZone).getMillis());
    }
}
