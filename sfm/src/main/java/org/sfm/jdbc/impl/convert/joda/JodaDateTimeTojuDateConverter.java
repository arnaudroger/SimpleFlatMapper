package org.sfm.jdbc.impl.convert.joda;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.sfm.utils.conv.Converter;

import java.util.Date;

public class JodaDateTimeTojuDateConverter implements Converter<DateTime, Date> {

    @Override
    public Date convert(DateTime in) throws Exception {
        if (in == null) return null;
        return in.toDate();
    }
}
