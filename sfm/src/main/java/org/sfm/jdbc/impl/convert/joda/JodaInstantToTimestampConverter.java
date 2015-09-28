package org.sfm.jdbc.impl.convert.joda;

import org.joda.time.Instant;
import org.sfm.utils.conv.Converter;
import java.sql.Timestamp;

public class JodaInstantToTimestampConverter implements Converter<Instant, Timestamp> {

    @Override
    public Timestamp convert(Instant in) throws Exception {
        if (in == null) return null;
        return new Timestamp(in.getMillis());
    }
}
