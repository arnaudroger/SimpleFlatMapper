package org.simpleflatmapper.converter.joda.impl.time;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.simpleflatmapper.converter.Converter;


public class JodaLocalDateTojuLocalDateConverter implements Converter<LocalDate, java.time.LocalDate> {


    @Override
    public java.time.LocalDate convert(LocalDate in) throws Exception {
        if (in == null) return null;
        return java.time.LocalDate.of(in.getYear(), in.getMonthOfYear(), in.getDayOfMonth());
    }
}
