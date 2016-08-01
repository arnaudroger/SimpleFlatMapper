package org.sfm.utils.conv.time;

import org.sfm.utils.conv.Converter;

import java.time.ZonedDateTime;
import java.util.Date;

public class JavaZonedDateTimeTojuDateConverter implements Converter<ZonedDateTime, Date> {
    @Override
    public Date convert(ZonedDateTime in) throws Exception {
        if (in == null) return null;
        return Date.from(in.toInstant());
    }
}
