package org.simpleflatmapper.jdbc.converter.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;

import java.sql.Time;
import java.time.LocalTime;

public class TimeToLocalTimeConverter implements Converter<Time, LocalTime> {
    @Override
    public LocalTime convert(Time in, Context context) throws Exception {
        if (in == null) return null;
        return in.toLocalTime();
    }
}
