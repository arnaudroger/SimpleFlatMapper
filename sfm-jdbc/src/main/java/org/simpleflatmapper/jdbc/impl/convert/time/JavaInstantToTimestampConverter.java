package org.simpleflatmapper.jdbc.impl.convert.time;

import org.simpleflatmapper.converter.Converter;

import java.sql.Timestamp;
import java.time.Instant;

public class JavaInstantToTimestampConverter implements Converter<Instant, Timestamp> {

    @Override
    public Timestamp convert(Instant in) throws Exception {
        if (in == null) return null;
        return new Timestamp(in.toEpochMilli());
    }
}
