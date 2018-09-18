package org.simpleflatmapper.converter.joda.impl;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.util.Date;

public class JodaLocalDateTimeTojuDateConverter implements ContextualConverter<LocalDateTime, Date> {
    private final DateTimeZone dateTimeZone;

    public JodaLocalDateTimeTojuDateConverter(DateTimeZone dateTimeZone) {
        this.dateTimeZone = dateTimeZone;
    }

    @Override
    public Date convert(LocalDateTime in, Context context) throws Exception {
        if (in == null) return null;
        return in.toDate(dateTimeZone.toTimeZone());
    }
}
