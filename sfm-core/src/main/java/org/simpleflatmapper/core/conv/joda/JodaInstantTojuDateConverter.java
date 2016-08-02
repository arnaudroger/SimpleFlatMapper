package org.simpleflatmapper.core.conv.joda;

import org.joda.time.Instant;
import org.simpleflatmapper.core.conv.Converter;

import java.util.Date;

public class JodaInstantTojuDateConverter implements Converter<Instant, Date> {

    @Override
    public Date convert(Instant in) throws Exception {
        if (in == null) return null;
        return in.toDate();
    }
}
