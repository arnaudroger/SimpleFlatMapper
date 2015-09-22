package org.sfm.jdbc.impl.convert.joda;

import org.joda.time.DateTime;
import org.sfm.utils.conv.Converter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class JodaLocalDateTimeToTimestampConverter implements Converter<LocalDateTime, Timestamp> {
    private final ZoneOffset offset;

    public JodaLocalDateTimeToTimestampConverter(ZoneOffset offset) {
        this.offset = offset;
    }

    @Override
    public Timestamp convert(LocalDateTime in) throws Exception {
        return new Timestamp(in.toInstant(offset).toEpochMilli());
    }
}
