package org.sfm.jdbc.impl.convert.time;

import org.sfm.utils.conv.Converter;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.Date;

public class JavaOffsetDateTimeTojuDateConverter implements Converter<OffsetDateTime, Date> {
    @Override
    public Date convert(OffsetDateTime in) throws Exception {
        if (in == null) return null;
        return Date.from(in.toInstant());
    }
}
