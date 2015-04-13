package org.sfm.jdbc.impl.getter.time;

import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.reflect.Getter;

import java.sql.ResultSet;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Date;


public class JavaLocalDateTimeResultSetGetter implements Getter<ResultSet, LocalDateTime> {
    private final int index;
    private final ZoneId zone;

    public JavaLocalDateTimeResultSetGetter(JdbcColumnKey key) {
        this.index = key.getIndex();
        this.zone = ZoneId.systemDefault();
    }

    @Override
    public LocalDateTime get(ResultSet target) throws Exception {
        Object o = target.getObject(index);

        if (o == null) {
            return null;
        }

        if (o instanceof Date) {
            return Instant.ofEpochMilli(((Date) o).getTime()).atZone(zone).toLocalDateTime();
        }

        if (o instanceof LocalDateTime) {
            return (LocalDateTime) o;
        }

        if (o instanceof TemporalAccessor) {
            return LocalDateTime.from((TemporalAccessor) o);
        }

        throw new IllegalArgumentException("Cannot convert " + o + " to LocalDateTime");
    }

    @Override
    public String toString() {
        return "JavaLocalDateTimeResultSetGetter{" +
                "column=" + index +
                '}';
    }
}
