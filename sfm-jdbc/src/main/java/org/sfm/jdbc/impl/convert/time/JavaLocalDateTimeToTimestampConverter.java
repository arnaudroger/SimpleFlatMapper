package org.sfm.jdbc.impl.convert.time;

import org.sfm.utils.conv.Converter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class JavaLocalDateTimeToTimestampConverter implements Converter<LocalDateTime, Timestamp> {
    private final ZoneId dateTimeZone;

    public JavaLocalDateTimeToTimestampConverter(ZoneId dateTimeZone) {
        this.dateTimeZone = dateTimeZone;
    }

    @Override
    public Timestamp convert(LocalDateTime in) throws Exception {
        if (in == null) return null;
        return new Timestamp(in.atZone(dateTimeZone).toInstant().toEpochMilli());
    }
}
