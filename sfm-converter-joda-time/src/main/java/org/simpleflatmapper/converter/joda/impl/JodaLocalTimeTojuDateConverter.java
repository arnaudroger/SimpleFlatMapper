package org.simpleflatmapper.converter.joda.impl;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.util.Date;

public class JodaLocalTimeTojuDateConverter implements ContextualConverter<LocalTime, Date> {
    private final DateTimeZone dateTimeZone;

    public JodaLocalTimeTojuDateConverter(DateTimeZone dateTimeZone) {
        this.dateTimeZone = dateTimeZone;
    }

    @Override
    public Date convert(LocalTime in, Context context) throws Exception {
        if (in == null) return null;
        return in.toDateTimeToday(dateTimeZone).toDate();
    }
}
