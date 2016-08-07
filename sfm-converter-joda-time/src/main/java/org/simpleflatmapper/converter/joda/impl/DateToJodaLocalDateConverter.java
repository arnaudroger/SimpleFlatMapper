package org.simpleflatmapper.converter.joda.impl;

import org.joda.time.LocalDate;
import org.simpleflatmapper.converter.Converter;

import java.util.Date;


public class DateToJodaLocalDateConverter implements Converter<Date, LocalDate> {

    @Override
    public LocalDate convert(Date in) throws Exception {
        if (in == null) return null;
        return LocalDate.fromDateFields(in);
    }
}
