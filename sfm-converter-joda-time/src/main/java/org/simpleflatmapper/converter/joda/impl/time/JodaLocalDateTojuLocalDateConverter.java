package org.simpleflatmapper.converter.joda.impl.time;

import org.joda.time.LocalDate;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;


public class JodaLocalDateTojuLocalDateConverter implements ContextualConverter<LocalDate, java.time.LocalDate> {


    @Override
    public java.time.LocalDate convert(LocalDate in, Context context) throws Exception {
        if (in == null) return null;
        return java.time.LocalDate.of(in.getYear(), in.getMonthOfYear(), in.getDayOfMonth());
    }
}
