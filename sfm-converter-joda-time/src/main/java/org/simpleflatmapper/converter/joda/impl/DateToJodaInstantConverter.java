package org.simpleflatmapper.converter.joda.impl;

import org.joda.time.Instant;
import org.joda.time.LocalTime;
import org.simpleflatmapper.converter.Converter;

import java.util.Date;


public class DateToJodaInstantConverter implements Converter<Date, Instant> {

    @Override
    public Instant convert(Date in) throws Exception {
        if (in == null) return null;
        return new Instant(in);
    }
}
