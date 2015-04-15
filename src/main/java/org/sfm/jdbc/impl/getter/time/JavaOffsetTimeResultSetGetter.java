package org.sfm.jdbc.impl.getter.time;

import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.reflect.Getter;

import java.sql.ResultSet;
import java.time.Instant;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Date;


public class JavaOffsetTimeResultSetGetter implements Getter<ResultSet, OffsetTime> {
    private final int index;
    private final ZoneId zone;

    public JavaOffsetTimeResultSetGetter(JdbcColumnKey key) {
        this.index = key.getIndex();
        this.zone = ZoneId.systemDefault();
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

        if (o instanceof TemporalAccessor) {
            return OffsetTime.from((TemporalAccessor) o);
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
