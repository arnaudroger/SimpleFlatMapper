package org.simpleflatmapper.jdbc.converter.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;

import java.sql.Time;
import java.time.OffsetTime;
import java.time.ZoneOffset;

public class TimeToOffsetTimeConverter implements ContextualConverter<Time, OffsetTime> {
    private final ZoneOffset offset;

    public TimeToOffsetTimeConverter(ZoneOffset offset) {
        this.offset = offset;
    }

    @Override
    public OffsetTime convert(Time in, Context context) throws Exception {
        if (in == null) return null;
        return in.toLocalTime().atOffset(offset);
    }
}
