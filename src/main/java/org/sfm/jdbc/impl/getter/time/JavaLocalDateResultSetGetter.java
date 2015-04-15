package org.sfm.jdbc.impl.getter.time;

import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.reflect.Getter;

import java.sql.ResultSet;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Date;


public class JavaLocalDateResultSetGetter implements Getter<ResultSet, LocalDate> {
    private final int index;
    private final ZoneId zone;

    public JavaLocalDateResultSetGetter(JdbcColumnKey key, ZoneId zoneId) {
        this.index = key.getIndex();
        this.zone = zoneId;
    }

    @Override
    public LocalDate get(ResultSet target) throws Exception {
        Object o = target.getObject(index);

        if (o == null) {
            return null;
        }

        if (o instanceof Date) {
            return Instant.ofEpochMilli(((Date) o).getTime()).atZone(zone).toLocalDate();
        }

        if (o instanceof LocalDate) {
            return (LocalDate) o;
        }

        if (o instanceof TemporalAccessor) {
            return LocalDate.from((TemporalAccessor) o);
        }

        throw new IllegalArgumentException("Cannot convert " + o + " to LocalDate");
    }

    @Override
    public String toString() {
        return "JavaLocalDateResultSetGetter{" +
                "column=" + index +
                '}';
    }
}
