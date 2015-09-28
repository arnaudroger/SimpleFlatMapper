package org.sfm.jdbc.impl.convert.time;

import org.sfm.utils.conv.Converter;

import java.sql.Timestamp;
import java.time.Instant;

public class JavaInstantToTimestampConverter implements Converter<Instant, Timestamp> {

    @Override
    public Timestamp convert(Instant in) throws Exception {
        if (in == null) return null;
        return new Timestamp(in.toEpochMilli());
    }
}
