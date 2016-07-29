package org.sfm.jdbc.impl.convert.joda;

import org.joda.time.LocalDate;
import org.sfm.utils.conv.Converter;

import java.sql.Date;

public class JodaLocalDateToDateConverter implements Converter<LocalDate, Date> {

    @Override
    public Date convert(LocalDate in) throws Exception {
        if (in == null) return null;
        return new Date(in.toDate().getTime());
    }
}
