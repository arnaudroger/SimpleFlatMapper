package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class JavaLocalDateTimeTojuDateConverter implements ContextualConverter<LocalDateTime, Date> {
    private final ZoneId dateTimeZone;

    public JavaLocalDateTimeTojuDateConverter(ZoneId dateTimeZone) {
        this.dateTimeZone = dateTimeZone;
    }

    @Override
    public Date convert(LocalDateTime in, Context context) throws Exception {
        if (in == null) return null;
        return Date.from(in.atZone(dateTimeZone).toInstant());
    }
}
