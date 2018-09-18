package org.simpleflatmapper.converter.joda.impl;

import org.joda.time.LocalDate;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.util.Date;


public class DateToJodaLocalDateConverter implements ContextualConverter<Date, LocalDate> {

    @Override
    public LocalDate convert(Date in, Context context) throws Exception {
        if (in == null) return null;
        return LocalDate.fromDateFields(in);
    }
}
