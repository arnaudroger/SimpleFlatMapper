package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Converter;

import java.time.OffsetDateTime;
import java.util.Date;

public class JavaOffsetDateTimeTojuDateConverter implements Converter<OffsetDateTime, Date> {
    @Override
    public Date convert(OffsetDateTime in) throws Exception {
        if (in == null) return null;
        return Date.from(in.toInstant());
    }
}
