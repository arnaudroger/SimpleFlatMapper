package org.simpleflatmapper.converter.joda.impl.time;

import org.joda.time.LocalDateTime;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;


public class JodaLocalDateTimeTojuLocalDateTimeConverter implements Converter<LocalDateTime, java.time.LocalDateTime> {


    @Override
    public java.time.LocalDateTime convert(LocalDateTime in, Context context) throws Exception {
        if (in == null) return null;
        return java.time.LocalDateTime.of(in.getYear(), in.getMonthOfYear(), in.getDayOfMonth(), in.getHourOfDay(), in.getMinuteOfHour(), in.getSecondOfMinute(), in.getMillisOfSecond() * 1000);
    }
}
