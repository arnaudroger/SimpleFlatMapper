package org.simpleflatmapper.core.conv.time;

import org.simpleflatmapper.core.conv.Converter;

import java.time.Instant;
import java.util.Date;

public class JavaInstantTojuDateConverter implements Converter<Instant, Date> {

    @Override
    public Date convert(Instant in) throws Exception {
        if (in == null) return null;
        return Date.from(in);
    }
}
