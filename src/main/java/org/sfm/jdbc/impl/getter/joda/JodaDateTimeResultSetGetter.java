package org.sfm.jdbc.impl.getter.joda;

import org.joda.time.DateTime;
import org.sfm.reflect.Getter;

import java.sql.ResultSet;
import java.util.Date;


public class JodaDateTimeResultSetGetter implements Getter<ResultSet, DateTime> {
    private final Getter<ResultSet, ? extends Date> getter;

    public JodaDateTimeResultSetGetter(Getter<ResultSet, ? extends Date> getter) {
        this.getter = getter;
    }

    @Override
    public DateTime get(ResultSet target) throws Exception {
        return new DateTime(getter.get(target));
    }

    @Override
    public String toString() {
        return "JodaDateTimeResultSetGetter{" +
                "getter=" + getter +
                '}';
    }
}
