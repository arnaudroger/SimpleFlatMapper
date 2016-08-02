package org.simpleflatmapper.core.conv.time;

import org.simpleflatmapper.core.conv.Converter;

import java.time.OffsetDateTime;
import java.util.Date;

public class JavaOffsetDateTimeTojuDateConverter implements Converter<OffsetDateTime, Date> {
    @Override
    public Date convert(OffsetDateTime in) throws Exception {
        if (in == null) return null;
        return Date.from(in.toInstant());
    }
}
