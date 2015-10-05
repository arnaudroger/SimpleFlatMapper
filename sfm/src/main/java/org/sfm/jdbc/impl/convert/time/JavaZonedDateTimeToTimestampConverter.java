package org.sfm.jdbc.impl.convert.time;

import org.sfm.utils.conv.Converter;

import java.sql.Timestamp;
import java.time.ZonedDateTime;

public class JavaZonedDateTimeToTimestampConverter implements Converter<ZonedDateTime, Timestamp> {
    @Override
    public Timestamp convert(ZonedDateTime in) throws Exception {
        if (in == null) return null;
        return new Timestamp(in.toInstant().toEpochMilli());
    }
}
