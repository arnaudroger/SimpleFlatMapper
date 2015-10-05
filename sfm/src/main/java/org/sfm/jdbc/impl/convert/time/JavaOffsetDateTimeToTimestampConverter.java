package org.sfm.jdbc.impl.convert.time;

import org.sfm.utils.conv.Converter;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

public class JavaOffsetDateTimeToTimestampConverter implements Converter<OffsetDateTime, Timestamp> {
    @Override
    public Timestamp convert(OffsetDateTime in) throws Exception {
        if (in == null) return null;
        return new Timestamp(in.toInstant().toEpochMilli());
    }
}
