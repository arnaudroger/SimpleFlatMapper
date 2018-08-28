package org.simpleflatmapper.converter.joda.impl;

import org.joda.time.Instant;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;

import java.util.Date;

public class JodaInstantTojuDateConverter implements Converter<Instant, Date> {

    @Override
    public Date convert(Instant in, Context context) throws Exception {
        if (in == null) return null;
        return in.toDate();
    }
}
