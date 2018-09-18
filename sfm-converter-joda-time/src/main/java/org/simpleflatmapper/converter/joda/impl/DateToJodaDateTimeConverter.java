package org.simpleflatmapper.converter.joda.impl;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.util.Date;


public class DateToJodaDateTimeConverter implements ContextualConverter<Date, DateTime> {
    private final DateTimeZone dateTimeZone;

    public DateToJodaDateTimeConverter(DateTimeZone dateTimeZone) {
        this.dateTimeZone = dateTimeZone;
    }

    @Override
    public DateTime convert(Date in, Context context) throws Exception {
        if (in == null) return null;
        return new DateTime(in, dateTimeZone);
    }
}
