package org.sfm.jdbc.impl.convert.time;

import org.sfm.utils.conv.Converter;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

public class JavaInstantTojuDateConverter implements Converter<Instant, Date> {

    @Override
    public Date convert(Instant in) throws Exception {
        if (in == null) return null;
        return Date.from(in);
    }
}
