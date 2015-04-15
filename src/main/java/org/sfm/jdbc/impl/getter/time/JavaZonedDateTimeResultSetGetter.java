package org.sfm.jdbc.impl.getter.time;

import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.reflect.Getter;

import java.sql.ResultSet;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Date;


public class JavaZonedDateTimeResultSetGetter implements Getter<ResultSet, ZonedDateTime> {
    private final int index;
    private final ZoneId zone;

    public JavaZonedDateTimeResultSetGetter(JdbcColumnKey key, ZoneId zoneId) {
        this.index = key.getIndex();
        this.zone = zoneId;
    }

    @Override
    public ZonedDateTime get(ResultSet target) throws Exception {
        Object o = target.getObject(index);

        if (o == null) {
            return null;
        }

        if (o instanceof Date) {
            return Instant.ofEpochMilli(((Date) o).getTime()).atZone(zone);
        }

        if (o instanceof ZonedDateTime) {
            return (ZonedDateTime) o;
        }

        if (o instanceof TemporalAccessor) {
            return ZonedDateTime.from((TemporalAccessor) o);
        }

        throw new IllegalArgumentException("Cannot convert " + o + " to ZonedDateTime");
    }

    @Override
    public String toString() {
        return "JavaZonedDateTimeResultSetGetter{" +
                "column=" + index +
                '}';
    }
}
