package org.simpleflatmapper.jdbc.impl.convert.joda;

import org.joda.time.DateTime;
import org.simpleflatmapper.converter.Converter;

import java.sql.Timestamp;

public class JodaDateTimeToTimestampConverter implements Converter<DateTime, Timestamp> {
    @Override
    public Timestamp convert(DateTime in) throws Exception {
        if (in == null) {
            return null;
        }
        return new Timestamp(in.getMillis());
    }
}
