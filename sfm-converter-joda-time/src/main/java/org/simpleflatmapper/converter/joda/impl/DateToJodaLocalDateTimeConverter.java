package org.simpleflatmapper.converter.joda.impl;

import org.joda.time.LocalDateTime;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.util.Date;


public class DateToJodaLocalDateTimeConverter implements ContextualConverter<Date, LocalDateTime> {
    @Override
    public LocalDateTime convert(Date in, Context context) throws Exception {
        if (in == null) return null;
        return LocalDateTime.fromDateFields(in);
    }
}
