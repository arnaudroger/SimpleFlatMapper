package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;

public class JavaLocalDateTojuDateConverter implements ContextualConverter<LocalDate, Date> {
    private final ZoneId dateTimeZone;

    public JavaLocalDateTojuDateConverter(ZoneId dateTimeZone) {
        this.dateTimeZone = dateTimeZone;
    }

    @Override
    public Date convert(LocalDate in, Context context) throws Exception {
        if (in == null) return null;
        return Date.from(in.atStartOfDay(dateTimeZone).toInstant());
    }
}
