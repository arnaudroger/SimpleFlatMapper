package org.sfm.jdbc.impl.getter.time;

import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.reflect.Getter;

import java.sql.ResultSet;
import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.Date;


public class JavaYearMonthResultSetGetter implements Getter<ResultSet, YearMonth> {
    private final int index;
    private final ZoneId zone;

    public JavaYearMonthResultSetGetter(JdbcColumnKey key, ZoneId zoneId) {
        this.index = key.getIndex();
        this.zone = zoneId;
    }

    @Override
    public YearMonth get(ResultSet target) throws Exception {
        Object o = target.getObject(index);

        if (o == null) {
            return null;
        }

        if (o instanceof Date) {
            final ZonedDateTime dateTime = Instant.ofEpochMilli(((Date) o).getTime()).atZone(zone);
            return YearMonth.of(dateTime.getYear(), dateTime.getMonth());
        }

        if (o instanceof Integer || o instanceof Long) {
            int l = ((Number)o).intValue();
            int year = l / 100;
            int month = l % 100;
            return YearMonth.of(year, month);
        }

        if (o instanceof TemporalAccessor) {
            return YearMonth.from((TemporalAccessor) o);
        }

        throw new IllegalArgumentException("Cannot convert " + o + " to YearMonth");
    }

    @Override
    public String toString() {
        return "JavaYearMonthResultSetGetter{" +
                "column=" + index +
                '}';
    }
}
