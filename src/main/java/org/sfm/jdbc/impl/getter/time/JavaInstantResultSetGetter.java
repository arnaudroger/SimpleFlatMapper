package org.sfm.jdbc.impl.getter.time;

import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.reflect.Getter;

import java.sql.ResultSet;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.Date;


public class JavaInstantResultSetGetter implements Getter<ResultSet, Instant> {
    private final int index;

    public JavaInstantResultSetGetter(JdbcColumnKey key) {
        this.index = key.getIndex();
    }

    @Override
    public Instant get(ResultSet target) throws Exception {
        Object o = target.getObject(index);

        if (o == null) {
            return null;
        }

        if (o instanceof Date) {
            return Instant.ofEpochMilli(((Date) o).getTime());
        }

        if (o instanceof TemporalAccessor) {
            return Instant.from((TemporalAccessor) o);
        }

        if (o instanceof Long || o instanceof Integer) {
            return Instant.ofEpochMilli(((Number)o).longValue());
        }

        throw new IllegalArgumentException("Cannot convert " + o + " to Instant");
    }

    @Override
    public String toString() {
        return "JavaInstantResultSetGetter{" +
                "column=" + index +
                '}';
    }
}
