package org.sfm.utils.conv.time;

import org.sfm.utils.conv.Converter;

import java.time.Instant;
import java.util.Date;

public class DateToJavaInstantConverter implements Converter<Date, Instant> {

    @Override
    public Instant convert(Date in) throws Exception {
        if (in == null) return null;
        return in.toInstant();
    }
}
