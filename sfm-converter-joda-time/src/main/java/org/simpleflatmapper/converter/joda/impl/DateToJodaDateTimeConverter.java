package org.simpleflatmapper.converter.joda.impl;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;

import java.util.Date;


public class DateToJodaDateTimeConverter implements Converter<Date, DateTime> {
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
