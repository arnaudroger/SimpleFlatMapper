package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.Instant;
import java.util.Date;

public class DateToJavaInstantConverter implements ContextualConverter<Date, Instant> {

    @Override
    public Instant convert(Date in, Context context) throws Exception {
        if (in == null) return null;
        return in.toInstant();
    }
}
