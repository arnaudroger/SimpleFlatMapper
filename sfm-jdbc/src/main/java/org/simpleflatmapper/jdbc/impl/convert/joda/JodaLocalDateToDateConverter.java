package org.simpleflatmapper.jdbc.impl.convert.joda;

import org.joda.time.LocalDate;
import org.simpleflatmapper.core.conv.Converter;

import java.sql.Date;

public class JodaLocalDateToDateConverter implements Converter<LocalDate, Date> {

    @Override
    public Date convert(LocalDate in) throws Exception {
        if (in == null) return null;
        return new Date(in.toDate().getTime());
    }
}
