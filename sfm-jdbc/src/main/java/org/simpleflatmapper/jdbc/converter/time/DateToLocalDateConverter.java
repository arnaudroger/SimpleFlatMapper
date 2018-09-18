package org.simpleflatmapper.jdbc.converter.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.sql.Date;
import java.time.LocalDate;

public class DateToLocalDateConverter implements ContextualConverter<Date, LocalDate> {
    @Override
    public LocalDate convert(Date in, Context context) throws Exception {
        if (in == null) return null;
        return in.toLocalDate();
    }
}
