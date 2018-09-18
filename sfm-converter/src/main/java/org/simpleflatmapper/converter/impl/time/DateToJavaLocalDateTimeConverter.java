package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateToJavaLocalDateTimeConverter implements ContextualConverter<Date, LocalDateTime> {
    private final ZoneId dateTimeZone;

    public DateToJavaLocalDateTimeConverter(ZoneId dateTimeZone) {
        this.dateTimeZone = dateTimeZone;
    }

    @Override
    public LocalDateTime convert(Date in, Context context) throws Exception {
        if (in == null) return null;
        return in.toInstant().atZone(dateTimeZone).toLocalDateTime();
    }
}
