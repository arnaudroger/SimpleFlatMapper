package org.sfm.jdbc.impl.getter.time;

import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.reflect.Getter;

import java.sql.ResultSet;
import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.Date;


public class JavaOffsetTimeResultSetGetter implements Getter<ResultSet, OffsetTime> {
    private final int index;
    private final ZoneId zone;

    public JavaOffsetTimeResultSetGetter(JdbcColumnKey key, ZoneId zoneId) {
        this.index = key.getIndex();
        this.zone = zoneId;
    }

    @Override
    public OffsetTime get(ResultSet target) throws Exception {
        Object o = target.getObject(index);

        if (o == null) {
            return null;
        }

        if (o instanceof Date) {
            final Instant instant = Instant.ofEpochMilli(((Date) o).getTime());
            return instant.atOffset(zone.getRules().getOffset(instant)).toOffsetTime();
        }

        if (o instanceof OffsetTime) {
            return (OffsetTime) o;
        }

        if (o instanceof LocalDateTime) {
            return ((LocalDateTime)o).atZone(zone).toOffsetDateTime().toOffsetTime();
        }

        if (o instanceof LocalTime) {
            return ((LocalTime)o).atOffset(zone.getRules().getStandardOffset(Instant.now()));
        }

        if (o instanceof TemporalAccessor) {
            return OffsetTime.from((TemporalAccessor)o);
        }

        throw new IllegalArgumentException("Cannot convert " + o + " to OffsetTime");
    }

    @Override
    public String toString() {
        return "JavaOffsetTimeResultSetGetter{" +
                "column=" + index +
                '}';
    }
}
