package org.simpleflatmapper.jdbc.impl.convert.time;

import org.simpleflatmapper.converter.Converter;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

public class JavaLocalTimeToTimeConverter implements Converter<LocalTime, Time> {
    private final ZoneId dateTimeZone;

    public JavaLocalTimeToTimeConverter(ZoneId dateTimeZone) {
        this.dateTimeZone = dateTimeZone;
    }

    @Override
    public Time convert(LocalTime in) throws Exception {
        if (in == null) return null;
        return new Time(in.atDate(LocalDate.now()).atZone(dateTimeZone).toInstant().toEpochMilli());
    }
}
