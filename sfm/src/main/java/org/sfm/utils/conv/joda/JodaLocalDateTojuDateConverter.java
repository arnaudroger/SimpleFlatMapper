package org.sfm.utils.conv.joda;

import org.joda.time.LocalDate;
import org.sfm.utils.conv.Converter;

import java.util.Date;

public class JodaLocalDateTojuDateConverter implements Converter<LocalDate, Date> {

    @Override
    public Date convert(LocalDate in) throws Exception {
        if (in == null) return null;
        return in.toDate();
    }
}
