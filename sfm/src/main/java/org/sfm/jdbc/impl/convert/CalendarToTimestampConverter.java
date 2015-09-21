package org.sfm.jdbc.impl.convert;

import java.sql.Timestamp;
import java.util.Calendar;

public class CalendarToTimestampConverter implements org.sfm.utils.conv.Converter<Calendar, Timestamp> {
    @Override
    public Timestamp convert(Calendar in) throws Exception {
        if (in != null) {
            return new Timestamp(in.getTimeInMillis());
        }
        return null;
    }
}
