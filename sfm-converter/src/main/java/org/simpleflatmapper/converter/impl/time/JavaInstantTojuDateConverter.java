package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.time.Instant;
import java.util.Date;

public class JavaInstantTojuDateConverter implements ContextualConverter<Instant, Date> {

    @Override
    public Date convert(Instant in, Context context) throws Exception {
        if (in == null) return null;
        return Date.from(in);
    }
}
