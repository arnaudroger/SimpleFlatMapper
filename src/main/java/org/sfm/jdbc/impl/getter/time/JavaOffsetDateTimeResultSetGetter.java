package org.sfm.jdbc.impl.getter.time;

import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.reflect.Getter;

import java.sql.ResultSet;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Date;


public class JavaOffsetDateTimeResultSetGetter implements Getter<ResultSet, OffsetDateTime> {
    private final int index;
    private final ZoneId zone;

    public JavaOffsetDateTimeResultSetGetter(JdbcColumnKey key, ZoneId zoneId) {
        this.index = key.getIndex();
        this.zone = zoneId;
    }

    @Override
    public OffsetDateTime get(ResultSet target) throws Exception {
        Object o = target.getObject(index);

        if (o == null) {
            return null;
        }

        if (o instanceof Date) {
            final Instant instant = Instant.ofEpochMilli(((Date) o).getTime());
            return instant.atOffset(zone.getRules().getOffset(instant));
        }

        if (o instanceof OffsetDateTime) {
            return (OffsetDateTime) o;
        }

        if (o instanceof TemporalAccessor) {
            return OffsetDateTime.from((TemporalAccessor) o);
        }

        throw new IllegalArgumentException("Cannot convert " + o + " to OffsetDateTime");
    }

    @Override
    public String toString() {
        return "JavaOffsetDateTimeResultSetGetter{" +
                "column=" + index +
                '}';
    }
}
