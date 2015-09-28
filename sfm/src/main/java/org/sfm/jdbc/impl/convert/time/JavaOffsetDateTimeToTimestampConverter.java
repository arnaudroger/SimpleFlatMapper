package org.sfm.jdbc.impl.convert.time;

import org.sfm.utils.conv.Converter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public class JavaOffsetDateTimeToTimestampConverter implements Converter<OffsetDateTime, Timestamp> {
    @Override
    public Timestamp convert(OffsetDateTime in) throws Exception {
        if (in == null) return null;
        return new Timestamp(in.toInstant().toEpochMilli());
    }
}
