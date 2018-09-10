package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;

import java.time.ZonedDateTime;
import java.util.Date;

public class JavaZonedDateTimeTojuDateConverter implements Converter<ZonedDateTime, Date> {
    @Override
    public Date convert(ZonedDateTime in, Context context) throws Exception {
        if (in == null) return null;
        return Date.from(in.toInstant());
    }
}
