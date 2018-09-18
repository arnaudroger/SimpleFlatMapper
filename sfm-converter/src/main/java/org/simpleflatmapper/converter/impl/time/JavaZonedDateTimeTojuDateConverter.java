package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.ZonedDateTime;
import java.util.Date;

public class JavaZonedDateTimeTojuDateConverter implements ContextualConverter<ZonedDateTime, Date> {
    @Override
    public Date convert(ZonedDateTime in, Context context) throws Exception {
        if (in == null) return null;
        return Date.from(in.toInstant());
    }
}
