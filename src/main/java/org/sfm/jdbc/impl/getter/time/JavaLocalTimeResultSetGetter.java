package org.sfm.jdbc.impl.getter.time;

import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.reflect.Getter;

import java.sql.ResultSet;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Date;


public class JavaLocalTimeResultSetGetter implements Getter<ResultSet, LocalTime> {
    private final int index;
    private final ZoneId zone;

    public JavaLocalTimeResultSetGetter(JdbcColumnKey key) {
        this.index = key.getIndex();
        this.zone = ZoneId.systemDefault();
    }

    @Override
    public LocalTime get(ResultSet target) throws Exception {
        Object o = target.getObject(index);

        if (o == null) {
            return null;
        }

        if (o instanceof Date) {
            return Instant.ofEpochMilli(((Date) o).getTime()).atZone(zone).toLocalTime();
        }

        if (o instanceof LocalTime) {
            return (LocalTime) o;
        }

        if (o instanceof TemporalAccessor) {
            return LocalTime.from((TemporalAccessor) o);
        }

        throw new IllegalArgumentException("Cannot convert " + o + " to LocalTime");
    }

    @Override
    public String toString() {
        return "JavaLocalTimeResultSetGetter{" +
                "column=" + index +
                '}';
    }
}
