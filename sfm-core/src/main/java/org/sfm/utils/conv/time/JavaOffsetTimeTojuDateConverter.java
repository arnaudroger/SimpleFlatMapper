package org.sfm.utils.conv.time;

import org.sfm.utils.conv.Converter;

import java.sql.Time;
import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.Date;

public class JavaOffsetTimeTojuDateConverter implements Converter<OffsetTime, Date> {
    @Override
    public Date convert(OffsetTime in) throws Exception {
        if (in == null) return null;
        return Date.from(in.atDate(LocalDate.now()).toInstant());
    }
}
