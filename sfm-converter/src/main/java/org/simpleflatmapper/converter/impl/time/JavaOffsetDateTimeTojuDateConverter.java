package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.OffsetDateTime;
import java.util.Date;

public class JavaOffsetDateTimeTojuDateConverter implements ContextualConverter<OffsetDateTime, Date> {
    @Override
    public Date convert(OffsetDateTime in, Context context) throws Exception {
        if (in == null) return null;
        return Date.from(in.toInstant());
    }
}
