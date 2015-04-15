package org.sfm.jdbc.impl.getter.time;

import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.reflect.Getter;

import java.sql.ResultSet;
import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.Date;


public class JavaYearResultSetGetter implements Getter<ResultSet, Year> {
    private final int index;
    private final ZoneId zone;

    public JavaYearResultSetGetter(JdbcColumnKey key, ZoneId zoneId) {
        this.index = key.getIndex();
        this.zone = zoneId;
    }

    @Override
    public Year get(ResultSet target) throws Exception {
        Object o = target.getObject(index);

        if (o == null) {
            return null;
        }

        if (o instanceof Date) {
            final ZonedDateTime dateTime = Instant.ofEpochMilli(((Date) o).getTime()).atZone(zone);
            return Year.of(dateTime.getYear());
        }

        if (o instanceof Integer || o instanceof Long) {
            return Year.of(((Number)o).intValue());
        }

        if (o instanceof Year) {
            return (Year) o;
        }

        if (o instanceof TemporalAccessor) {
            return Year.from((TemporalAccessor) o);
        }

        throw new IllegalArgumentException("Cannot convert " + o + " to Year");
    }

    @Override
    public String toString() {
        return "JavaYearResultSetGetter{" +
                "column=" + index +
                '}';
    }
}
