package org.simpleflatmapper.jdbc.converter;

import org.simpleflatmapper.converter.Converter;

import java.sql.Timestamp;
import java.util.Calendar;

public class CalendarToTimestampConverter implements Converter<Calendar, Timestamp> {
    @Override
    public Timestamp convert(Calendar in) throws Exception {
        if (in != null) {
            return new Timestamp(in.getTimeInMillis());
        }
        return null;
    }
}
