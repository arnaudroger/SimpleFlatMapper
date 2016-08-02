package org.simpleflatmapper.core.conv.time;

import org.simpleflatmapper.core.conv.Converter;

import java.time.Instant;
import java.util.Date;

public class DateToJavaInstantConverter implements Converter<Date, Instant> {

    @Override
    public Instant convert(Date in) throws Exception {
        if (in == null) return null;
        return in.toInstant();
    }
}
