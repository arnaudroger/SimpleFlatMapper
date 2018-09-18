package org.simpleflatmapper.converter.joda.impl;

import org.joda.time.LocalDate;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.util.Date;

public class JodaLocalDateTojuDateConverter implements ContextualConverter<LocalDate, Date> {

    @Override
    public Date convert(LocalDate in, Context context) throws Exception {
        if (in == null) return null;
        return in.toDate();
    }
}
