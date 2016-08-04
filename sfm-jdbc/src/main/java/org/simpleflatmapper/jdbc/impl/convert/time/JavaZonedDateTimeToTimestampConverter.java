package org.simpleflatmapper.jdbc.impl.convert.time;

import org.simpleflatmapper.converter.Converter;

import java.sql.Timestamp;
import java.time.ZonedDateTime;

public class JavaZonedDateTimeToTimestampConverter implements Converter<ZonedDateTime, Timestamp> {
    @Override
    public Timestamp convert(ZonedDateTime in) throws Exception {
        if (in == null) return null;
        return new Timestamp(in.toInstant().toEpochMilli());
    }
}
