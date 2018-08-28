package org.simpleflatmapper.converter.joda.impl;

import org.joda.time.LocalDateTime;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;

import java.util.Date;


public class DateToJodaLocalDateTimeConverter implements Converter<Date, LocalDateTime> {
    @Override
    public LocalDateTime convert(Date in, Context context) throws Exception {
        if (in == null) return null;
        return LocalDateTime.fromDateFields(in);
    }
}
