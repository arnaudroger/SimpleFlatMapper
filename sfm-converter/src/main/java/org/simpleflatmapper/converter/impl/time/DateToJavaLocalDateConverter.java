package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateToJavaLocalDateConverter implements ContextualConverter<Date, LocalDate> {
    private final ZoneId dateTimeZone;

    public DateToJavaLocalDateConverter(ZoneId dateTimeZone) {
        this.dateTimeZone = dateTimeZone;
    }

    @Override
    public LocalDate convert(Date in, Context context) throws Exception {
        if (in == null) return null;
        return in.toInstant().atZone(dateTimeZone).toLocalDate();
    }
}
