package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Converter;

import java.time.ZonedDateTime;
import java.util.Date;

public class JavaZonedDateTimeTojuDateConverter implements Converter<ZonedDateTime, Date> {
    @Override
    public Date convert(ZonedDateTime in) throws Exception {
        if (in == null) return null;
        return Date.from(in.toInstant());
    }
}
