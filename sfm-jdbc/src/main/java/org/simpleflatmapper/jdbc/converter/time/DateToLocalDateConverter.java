package org.simpleflatmapper.jdbc.converter.time;

import org.simpleflatmapper.converter.Converter;

import java.sql.Date;
import java.time.LocalDate;

public class DateToLocalDateConverter implements Converter<Date, LocalDate> {
    @Override
    public LocalDate convert(Date in) throws Exception {
        if (in == null) return null;
        return in.toLocalDate();
    }
}
